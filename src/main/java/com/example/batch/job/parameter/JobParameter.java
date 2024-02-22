package com.example.batch.job.parameter;

import com.example.batch.member.enumeration.MemberStatus;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class JobParameter {
    private LocalDateTime dateTime;
    private LocalDate birthDay;
    private MemberStatus status;
    private String name;

    @Value("#{jobParameters[dateTime]}")
    public void setDateTime(String strDateTime) {
        if (StringUtils.hasText(strDateTime)) {
            this.dateTime = LocalDateTime.parse(strDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    @Value("#{jobParameters[birthDay]}")
    public void setBirthDay(String strBirthDay) {
        if (StringUtils.hasText(strBirthDay)) {
            this.birthDay = LocalDate.parse(strBirthDay, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }

    @Value("#{jobParameters[status]}")
    public void setStatus(String strStatus) {
        if (StringUtils.hasText(strStatus)) {
            this.status = MemberStatus.valueOf(strStatus);
        }
    }

    @Value("#{jobParameters[name]}")
    public void setName(String name) {
        if (StringUtils.hasText(name)) {
            this.name = name;
        }
    }
}
