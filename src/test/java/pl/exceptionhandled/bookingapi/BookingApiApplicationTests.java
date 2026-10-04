package pl.exceptionhandled.bookingapi;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import pl.exceptionhandled.bookingapi.event.EventController;
import pl.exceptionhandled.bookingapi.event.EventService;

@WebMvcTest(EventController.class)
class BookingApiApplicationTests {

	@MockitoBean
	EventService eventService;

	@Test
	void contextLoads() {
	}

}
