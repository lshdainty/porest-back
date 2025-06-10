package com.lshdainty.myhr.api;

import com.lshdainty.myhr.domain.User;
import com.lshdainty.myhr.dto.UserDto;
import com.lshdainty.myhr.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserApiController {
    private final UserService userService;

    @GetMapping("/api/v1/test")
    public ApiResponse test() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ApiResponse.success();
    }

    @PostMapping("/api/v1/user")
    public ApiResponse join(@RequestBody UserDto userDto) {
        Long userId = userService.join(
                userDto.getUserName(),
                userDto.getUserBirth(),
                userDto.getUserEmploy(),
                userDto.getUserWorkTime(),
                userDto.getLunarYN()
        );

        return ApiResponse.success(UserDto.builder().userNo(userId).build());
    }

    @GetMapping("/api/v1/user/{id}")
    public ApiResponse user(@PathVariable("id") Long userId) {
        User user = userService.findUser(userId);

        return ApiResponse.success(UserDto
                .builder()
                .userNo(userId)
                .userName(user.getName())
                .userBirth(user.getBirth())
                .userEmploy(user.getEmploy())
                .userWorkTime(user.getWorkTime())
                .lunarYN(user.getLunarYN())
                .build()
        );
    }

    @GetMapping("/api/v1/users")
    public ApiResponse users() {
        List<User> users = userService.findUsers();

        List<UserDto> resps = users.stream()
                .map(u -> UserDto
                        .builder()
                        .userNo(u.getId())
                        .userName(u.getName())
                        .userBirth(u.getBirth())
                        .userEmploy(u.getEmploy())
                        .userWorkTime(u.getWorkTime())
                        .lunarYN(u.getLunarYN())
                        .build()
                )
                .toList();

        return ApiResponse.success(resps);
    }

    @PutMapping("/api/v1/user/{id}")
    public ApiResponse editUser(@PathVariable("id") Long userId, @RequestBody UserDto userDto) {
        userService.editUser(
                userId,
                userDto.getUserName(),
                userDto.getUserBirth(),
                userDto.getUserEmploy(),
                userDto.getUserWorkTime(),
                userDto.getLunarYN()
        );

        User findUser = userService.findUser(userId);

        return ApiResponse.success(UserDto
                .builder()
                .userNo(userId)
                .userName(findUser.getName())
                .userBirth(findUser.getBirth())
                .userEmploy(findUser.getEmploy())
                .userWorkTime(findUser.getWorkTime())
                .lunarYN(findUser.getLunarYN())
                .build()
        );
    }

    @DeleteMapping("/api/v1/user/{id}")
    public ApiResponse deleteUser(@PathVariable("id") Long userId) {
        userService.deleteUser(userId);
        return ApiResponse.success();
    }
}
