package com.soremed.backend.controller;


import com.soremed.backend.dto.ErrorDTO;
import com.soremed.backend.dto.LoginDTO;
import com.soremed.backend.dto.UserDTO;
import com.soremed.backend.entity.Order;
import com.soremed.backend.entity.User;
import com.soremed.backend.service.OrderService;
import com.soremed.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final OrderService orderService;
    public UserController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO creds) {
        Optional<User> opt = userService.authenticate(creds.getUsername(), creds.getPassword());
        if (opt.isPresent()) {
            User user = opt.get();
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setRole(user.getRole().name());
            return ResponseEntity
                    .ok()      // 200 OK
                    .body(dto); // UserDTO dans le corps
        } else {
            ErrorDTO err = new ErrorDTO("Nom d'utilisateur ou mot de passe incorrect");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // 401
                    .body(err);                      // ErrorDTO dans le corps
        }
    }

    // ce endpoint renvoie l utilisateur courant apres l'authentification (evite l utilisateur de se reconnecter a chaque fois)
    @GetMapping("/users/me")
    public ResponseEntity<UserDTO> whoami(Authentication auth) {
        return userService.getUserByUsername(auth.getName())
                .map(u -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(u.getId());
                    dto.setUsername(u.getUsername());
                    dto.setRole(u.getRole().name());
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }






    // 2. Récupérer la liste des utilisateurs (optionnel, admin only)
    @GetMapping("/users")
    public List<User> getAllUsers() {
        // utile seulement si on prévoit une gestion des utilisateurs côté admin
        // ici on appelle directement le repository via service si besoin
        return userService.getAllUsers();
    }

    // 3. Statistiques : nombre de commandes par mois (dernière année par ex) et distribution des statuts
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        // Exemples de calculs de stats simples :

        // a) commandes par mois (sur 12 derniers mois)
        int[] ordersPerMonth = new int[12];
        List<Order> allOrders = orderService.listAllOrders();
        for (Order order : allOrders) {
            Date date = order.getOrderDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int month = cal.get(Calendar.MONTH); // 0 = Janvier, 11 = Dec
            ordersPerMonth[month] += 1;
        }
        stats.put("ordersPerMonth", ordersPerMonth);

        // b) distribution par statut
        Map<String, Integer> statusCount = new HashMap<>();
        for (Order order : allOrders) {
            String status = order.getStatus();
            statusCount.put(status, statusCount.getOrDefault(status, 0) + 1);
        }
        stats.put("statusDistribution", statusCount);

        return stats;
    }

    @PostMapping("/users/register")
    public User register(@RequestBody User newUser) {
        // On peut ajouter ici une logique de validation.
        // Pour l'instant, on peut directement sauvegarder l'utilisateur via le service.
        // Assure-toi que UserRepository et UserService gèrent la création d'un nouvel utilisateur.
        return userService.save(newUser);  // Tu devras implémenter la méthode save dans UserService et dans ton repository.
    }

}

