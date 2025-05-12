package com.soremed.backend.controller;


import com.soremed.backend.dto.*;
import com.soremed.backend.entity.Order;
import com.soremed.backend.entity.User;
import com.soremed.backend.enums.Role;
import com.soremed.backend.service.OrderService;
import com.soremed.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * 3.1. Liste de tous les utilisateurs — accessible aux ADMIN seulement.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public List<UserDTO> listAllUsersAdmin() {
        return userService.getAllUsers().stream()
                .map(u -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(u.getId());
                    dto.setUsername(u.getUsername());
                    dto.setRole(u.getRole().name());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users")
    public ResponseEntity<UserDTO> createAdminUser(@RequestBody AdminUserDTO dto) {
        User u = new User();
        u.setUsername(dto.getUsername());
        u.setPassword(dto.getPassword());
        u.setRole(Role.valueOf(dto.getRole()));
        User saved = userService.save(u);
        UserDTO out = new UserDTO();
        out.setId(saved.getId());
        out.setUsername(saved.getUsername());
        out.setRole(saved.getRole().name());
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }
    /**
     * 3.2. Changer le rôle d’un utilisateur — ADMIN only.
     *    ex. PUT /api/admin/users/42/role?newRole=SERVICE_ACHAT
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/users/{id}/role")
    public UserDTO updateUserRole(
            @PathVariable Long id,
            @RequestParam("newRole") Role newRole
    ) {
        User updated = userService.changeRole(id, newRole);
        UserDTO dto = new UserDTO();
        dto.setId(updated.getId());
        dto.setUsername(updated.getUsername());
        dto.setRole(updated.getRole().name());
        return dto;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("admin/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/users/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegistrationDTO dto) {
        User u = new User();
        u.setUsername(dto.getUsername());
        u.setPassword(dto.getPassword());
        u.setRole(Role.CLIENT);
        u.setIceNumber(dto.getIceNumber());
        u.setAddress(dto.getAddress());
        u.setPhone(dto.getPhone());
        User saved = userService.save(u);
        UserDTO out = new UserDTO();
        out.setId(saved.getId());
        out.setUsername(saved.getUsername());
        out.setRole(saved.getRole().name());
        return ResponseEntity.ok(out);
    }

}

