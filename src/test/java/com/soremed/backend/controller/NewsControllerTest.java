//package com.soremed.backend.controller;
//
//import com.soremed.backend.entity.News;
//import com.soremed.backend.service.NewsService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(SpringExtension.class)
//@WebMvcTest(controllers = NewsController.class,
//        properties = "news.images.dir=./target/test-uploads"  // ② fournis la propriété nécessaire
//)
//@AutoConfigureMockMvc(addFilters = false)                  // ① désactive Spring Security
//class NewsControllerTest {
//
//    @Autowired
//    private MockMvc mvc;
//
//    @MockitoBean   // ou @MockitoBean selon ta version
//    private NewsService service;
//
//    @Test
//    void list_returnsAllNews() throws Exception {
//        News n1 = new News(); n1.setId(1L); n1.setTitle("T1");
//        News n2 = new News(); n2.setId(2L); n2.setTitle("T2");
//        when(service.listAll()).thenReturn(List.of(n1, n2));
//
//        mvc.perform(get("/api/news"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[1].title").value("T2"));
//    }
//
//    @Test
//    void uploadImage_returnsBadRequest_onEmptyFile() throws Exception {
//        MockMultipartFile empty = new MockMultipartFile(
//                "file", "", "image/png", new byte[0]
//        );
//        mvc.perform(multipart("/api/news/upload").file(empty))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("Fichier vide"));
//    }
//}
