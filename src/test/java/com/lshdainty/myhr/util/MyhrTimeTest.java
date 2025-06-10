package com.lshdainty.myhr.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("myHR Time Util 테스트")
class MyhrTimeTest {
    @Test
    @DisplayName("시작 시간이 종료 시간 이후인지 확인 - 성공")
    void isAfterThanEndDateSuccessTest() {
        assertThat(MyhrTime.isAfterThanEndDate(
                LocalDateTime.of(2025, 1, 2, 0, 0, 0),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0))
        ).isTrue();
    }

    @Test
    @DisplayName("시작 시간이 종료 시간 이후인지 확인 - 실패 (시작 시간이 종료 시간 이전임)")
    void isAfterThanEndDateFailTest() {
        assertThat(MyhrTime.isAfterThanEndDate(
                LocalDateTime.of(2025, 1, 1, 0, 0, 0),
                LocalDateTime.of(2025, 1, 2, 0, 0, 0))
        ).isFalse();
    }

    @Test
    @DisplayName("시작 시간이 종료 시간 이후인지 확인 - 실패 (시작 시간과 종료 시간 동일함)")
    void isAfterThanEndDateSuccessTestSameTime() {
        assertThat(MyhrTime.isAfterThanEndDate(
                LocalDateTime.of(2025, 1, 1, 0, 0, 0),
                LocalDateTime.of(2025, 1, 1, 0, 0, 0))
        ).isFalse();
    }

    @Test
    @DisplayName("시작 시간과 종료 시간 사이의 날짜 목록 구하기 - 성공")
    void getBetweenDatesSuccessTest() {
        assertThat(MyhrTime.getBetweenDates(
                LocalDateTime.of(2025, 1, 1, 0, 0, 0),
                LocalDateTime.of(2025, 1, 5, 0, 0, 0)
        )).containsExactly(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 2),
                LocalDate.of(2025, 1, 3),
                LocalDate.of(2025, 1, 4),
                LocalDate.of(2025, 1, 5)
        );
    }

    @Test
    void getBetweenDatesByDayOfWeek() {
    }

    @Test
    void addAllDates() {
    }

    @Test
    void removeAllDates() {
    }

    @Test
    void findMaxDateTime() {
    }

    @Test
    void findMinDateTime() {
    }
}