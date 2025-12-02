package com.lshdainty.porest.service;

import com.lshdainty.porest.common.exception.EntityNotFoundException;
import com.lshdainty.porest.common.type.CountryCode;
import com.lshdainty.porest.common.type.YNType;
import com.lshdainty.porest.holiday.domain.Holiday;
import com.lshdainty.porest.holiday.repository.HolidayRepository;
import com.lshdainty.porest.holiday.service.HolidayService;
import com.lshdainty.porest.holiday.service.dto.HolidayServiceDto;
import com.lshdainty.porest.holiday.type.HolidayType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("공휴일 서비스 테스트")
class HolidayServiceTest {
    @Mock
    private HolidayRepository holidayRepository;

    @InjectMocks
    private HolidayService holidayService;

    @Nested
    @DisplayName("공휴일 등록")
    class RegistHoliday {
        @Test
        @DisplayName("성공 - 공휴일이 정상적으로 저장된다")
        void registHolidaySuccess() {
            // given
            HolidayServiceDto data = HolidayServiceDto.builder()
                    .name("설날")
                    .date(LocalDate.of(2025, 1, 29))
                    .type(HolidayType.PUBLIC)
                    .countryCode(CountryCode.KR)
                    .lunarYN(YNType.Y)
                    .lunarDate(LocalDate.of(2025, 1, 1))
                    .isRecurring(YNType.Y)
                    .icon("🎉")
                    .build();
            willDoNothing().given(holidayRepository).save(any(Holiday.class));

            // when
            holidayService.registHoliday(data);

            // then
            then(holidayRepository).should().save(any(Holiday.class));
        }
    }

    @Nested
    @DisplayName("공휴일 단건 조회")
    class FindById {
        @Test
        @DisplayName("성공 - 존재하는 공휴일을 반환한다")
        void findByIdSuccess() {
            // given
            Long seq = 1L;
            Holiday holiday = Holiday.createHoliday("설날", LocalDate.of(2025, 1, 29), HolidayType.PUBLIC, CountryCode.KR, YNType.Y, LocalDate.of(2025, 1, 1), YNType.Y, "🎉");
            setHolidaySeq(holiday, seq);
            given(holidayRepository.findById(seq)).willReturn(Optional.of(holiday));

            // when
            Holiday result = holidayService.findById(seq);

            // then
            then(holidayRepository).should().findById(seq);
            assertThat(result.getName()).isEqualTo("설날");
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 공휴일이면 예외가 발생한다")
        void findByIdFailNotFound() {
            // given
            Long seq = 999L;
            given(holidayRepository.findById(seq)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> holidayService.findById(seq))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("국가별 공휴일 조회")
    class FindHolidays {
        @Test
        @DisplayName("성공 - 국가 코드로 공휴일 목록을 조회한다")
        void findHolidaysSuccess() {
            // given
            CountryCode countryCode = CountryCode.KR;
            List<Holiday> holidays = List.of(
                    Holiday.createHoliday("설날", LocalDate.of(2025, 1, 29), HolidayType.PUBLIC, CountryCode.KR, YNType.Y, LocalDate.of(2025, 1, 1), YNType.Y, "🎉"),
                    Holiday.createHoliday("추석", LocalDate.of(2025, 10, 6), HolidayType.PUBLIC, CountryCode.KR, YNType.Y, LocalDate.of(2025, 8, 15), YNType.Y, "🌕")
            );
            given(holidayRepository.findHolidays(countryCode)).willReturn(holidays);

            // when
            List<Holiday> result = holidayService.findHolidays(countryCode);

            // then
            then(holidayRepository).should().findHolidays(countryCode);
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("성공 - 공휴일이 없을 경우 빈 리스트가 반환된다")
        void findHolidaysEmptyList() {
            // given
            CountryCode countryCode = CountryCode.US;
            given(holidayRepository.findHolidays(countryCode)).willReturn(List.of());

            // when
            List<Holiday> result = holidayService.findHolidays(countryCode);

            // then
            then(holidayRepository).should().findHolidays(countryCode);
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("기간별 공휴일 조회")
    class SearchHolidaysByStartEndDate {
        @Test
        @DisplayName("성공 - 시작일과 종료일 사이의 공휴일을 조회한다")
        void searchHolidaysByStartEndDateSuccess() {
            // given
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);
            CountryCode countryCode = CountryCode.KR;
            List<Holiday> holidays = List.of(
                    Holiday.createHoliday("설날", LocalDate.of(2025, 1, 29), HolidayType.PUBLIC, CountryCode.KR, YNType.Y, LocalDate.of(2025, 1, 1), YNType.Y, "🎉")
            );
            given(holidayRepository.findHolidaysByStartEndDate(startDate, endDate, countryCode)).willReturn(holidays);

            // when
            List<Holiday> result = holidayService.searchHolidaysByStartEndDate(startDate, endDate, countryCode);

            // then
            then(holidayRepository).should().findHolidaysByStartEndDate(startDate, endDate, countryCode);
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("타입별 공휴일 조회")
    class SearchHolidaysByType {
        @Test
        @DisplayName("성공 - 공휴일 타입으로 목록을 조회한다")
        void searchHolidaysByTypeSuccess() {
            // given
            HolidayType type = HolidayType.PUBLIC;
            List<Holiday> holidays = List.of(
                    Holiday.createHoliday("설날", LocalDate.of(2025, 1, 29), HolidayType.PUBLIC, CountryCode.KR, YNType.Y, LocalDate.of(2025, 1, 1), YNType.Y, "🎉")
            );
            given(holidayRepository.findHolidaysByType(type)).willReturn(holidays);

            // when
            List<Holiday> result = holidayService.searchHolidaysByType(type);

            // then
            then(holidayRepository).should().findHolidaysByType(type);
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("공휴일 수정")
    class EditHoliday {
        @Test
        @DisplayName("성공 - 공휴일 정보가 수정된다")
        void editHolidaySuccess() {
            // given
            Long seq = 1L;
            Holiday holiday = Holiday.createHoliday("설날", LocalDate.of(2025, 1, 29), HolidayType.PUBLIC, CountryCode.KR, YNType.Y, LocalDate.of(2025, 1, 1), YNType.Y, "🎉");
            setHolidaySeq(holiday, seq);
            given(holidayRepository.findById(seq)).willReturn(Optional.of(holiday));

            HolidayServiceDto data = HolidayServiceDto.builder()
                    .seq(seq)
                    .name("설날 연휴")
                    .date(LocalDate.of(2025, 1, 30))
                    .build();

            // when
            holidayService.editHoliday(data);

            // then
            then(holidayRepository).should().findById(seq);
            assertThat(holiday.getName()).isEqualTo("설날 연휴");
            assertThat(holiday.getDate()).isEqualTo(LocalDate.of(2025, 1, 30));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 공휴일을 수정하려 하면 예외가 발생한다")
        void editHolidayFailNotFound() {
            // given
            Long seq = 999L;
            HolidayServiceDto data = HolidayServiceDto.builder().seq(seq).build();
            given(holidayRepository.findById(seq)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> holidayService.editHoliday(data))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("공휴일 삭제")
    class DeleteHoliday {
        @Test
        @DisplayName("성공 - 공휴일이 삭제된다")
        void deleteHolidaySuccess() {
            // given
            Long seq = 1L;
            Holiday holiday = Holiday.createHoliday("설날", LocalDate.of(2025, 1, 29), HolidayType.PUBLIC, CountryCode.KR, YNType.Y, LocalDate.of(2025, 1, 1), YNType.Y, "🎉");
            given(holidayRepository.findById(seq)).willReturn(Optional.of(holiday));
            willDoNothing().given(holidayRepository).delete(holiday);

            // when
            holidayService.deleteHoliday(seq);

            // then
            then(holidayRepository).should().findById(seq);
            then(holidayRepository).should().delete(holiday);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 공휴일을 삭제하려 하면 예외가 발생한다")
        void deleteHolidayFailNotFound() {
            // given
            Long seq = 999L;
            given(holidayRepository.findById(seq)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> holidayService.deleteHoliday(seq))
                    .isInstanceOf(EntityNotFoundException.class);
            then(holidayRepository).should(never()).delete(any(Holiday.class));
        }
    }

    @Nested
    @DisplayName("공휴일 존재 확인")
    class CheckHolidayExist {
        @Test
        @DisplayName("성공 - 존재하는 공휴일을 반환한다")
        void checkHolidayExistSuccess() {
            // given
            Long seq = 1L;
            Holiday holiday = Holiday.createHoliday("설날", LocalDate.of(2025, 1, 29), HolidayType.PUBLIC, CountryCode.KR, YNType.Y, LocalDate.of(2025, 1, 1), YNType.Y, "🎉");
            given(holidayRepository.findById(seq)).willReturn(Optional.of(holiday));

            // when
            Holiday result = holidayService.checkHolidayExist(seq);

            // then
            assertThat(result).isEqualTo(holiday);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 공휴일이면 예외가 발생한다")
        void checkHolidayExistFailNotFound() {
            // given
            Long seq = 999L;
            given(holidayRepository.findById(seq)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> holidayService.checkHolidayExist(seq))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    // 테스트 헬퍼 메서드
    private void setHolidaySeq(Holiday holiday, Long seq) {
        try {
            java.lang.reflect.Field field = Holiday.class.getDeclaredField("seq");
            field.setAccessible(true);
            field.set(holiday, seq);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
