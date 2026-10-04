package pl.exceptionhandled.bookingapi.event;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.exceptionhandled.bookingapi.event.dto.CreateEventRequest;
import pl.exceptionhandled.bookingapi.event.dto.EventResponse;
import pl.exceptionhandled.bookingapi.security.SecurityConfig;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@Import(SecurityConfig.class)
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    @Test
    void shouldReturn401ForAnonymousUser() throws Exception {
        var eventRequest = new CreateEventRequest(
                "name",
                "place",
                Instant.now().plusSeconds(86400)
        );

        mockMvc.perform(
                        post("/api/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(eventRequest))
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(eventService);
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = "USER")
    void shouldReturn403ForRegularUser() throws Exception {
        var eventRequest = new CreateEventRequest(
                "name",
                "place",
                Instant.now().plusSeconds(86400)
        );

        mockMvc.perform(
                        post("/api/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(eventRequest))
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(eventService);
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = "ADMIN")
    void shouldReturn201ForAdmin() throws Exception {
        var eventRequest = new CreateEventRequest(
                "name",
                "place",
                Instant.now().plusSeconds(86400)
        );

        var eventResponse = new EventResponse(1L, "name", "place", Instant.now().plusSeconds(86400), Instant.now());

        when(eventService.create(any()))
                .thenReturn(eventResponse);

        mockMvc.perform(
                        post("/api/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(eventRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location","/api/events/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L));

        verify(eventService, times(1))
                .create(any(CreateEventRequest.class));
    }
}
