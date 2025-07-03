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

    @PostMapping("/api/v1/user")
    public ApiResponse join(@RequestBody UserDto userDto) {
        String userId = userService.join(
                userDto.getUserId(),
                userDto.getUserPwd(),
                userDto.getUserName(),
                userDto.getUserEmail(),
                userDto.getUserBirth(),
                userDto.getUserEmploy(),
                userDto.getUserWorkTime(),
                userDto.getLunarYN()
        );

        return ApiResponse.success(UserDto.builder().userId(userId).build());
    }

    @GetMapping("/api/v1/user/{id}")
    public ApiResponse user(@PathVariable("id") String userId) {
        User user = userService.findUser(userId);

        return ApiResponse.success(UserDto
                .builder()
                .userId(userId)
                .userName(user.getName())
                .userEmail(user.getEmail())
                .userBirth(user.getBirth())
                .userWorkTime(user.getWorkTime())
                .userRole(user.getRole().name())
                .userEmploy(user.getEmploy())
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
                        .userId(u.getId())
                        .userName(u.getName())
                        .userEmail(u.getEmail())
                        .userBirth(u.getBirth())
                        .userWorkTime(u.getWorkTime())
                        .userRole(u.getRole().name())
                        .userEmploy(u.getEmploy())
                        .lunarYN(u.getLunarYN())
                        .build()
                )
                .toList();

        return ApiResponse.success(resps);
    }

    @PutMapping("/api/v1/user/{id}")
    public ApiResponse editUser(@PathVariable("id") String userId, @RequestBody UserDto userDto) {
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
                .userId(userId)
                .userName(findUser.getName())
                .userEmail(findUser.getEmail())
                .userBirth(findUser.getBirth())
                .userWorkTime(findUser.getWorkTime())
                .userRole(findUser.getRole().name())
                .userEmploy(findUser.getEmploy())
                .lunarYN(findUser.getLunarYN())
                .build()
        );
    }

    @DeleteMapping("/api/v1/user/{id}")
    public ApiResponse deleteUser(@PathVariable("id") String userId) {
        userService.deleteUser(userId);
        return ApiResponse.success();
    }
}
