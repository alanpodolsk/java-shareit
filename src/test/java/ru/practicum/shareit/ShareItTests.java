package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ShareItTests {
	EasyRandom generator = new EasyRandom();
	private final UserService userService;

	@Test
	@DisplayName("Должен вывести всех пользователей")
	void shouldGetAllUsers() {
		//Arrange
		UserDto user1 = userService.createUser(generator.nextObject(UserDto.class));
		UserDto user2 = userService.createUser(generator.nextObject(UserDto.class));
		//Act
		List<UserDto> savedUsers = userService.getAllUsers();
		//Assert
		Assertions.assertArrayEquals(new UserDto[]{user1,user2},savedUsers.toArray());
	}

}
