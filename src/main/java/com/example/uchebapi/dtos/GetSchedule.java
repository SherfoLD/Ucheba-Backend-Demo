package com.example.uchebapi.dtos;

import com.example.uchebapi.domain.ScheduleItem;
import com.example.uchebapi.enums.LessonTypes;

import java.util.*;

public class GetSchedule {
    private final List<ScheduleItem> scheduleItemList;
    private final Integer scheduleVersion;
    public GetSchedule(List<ScheduleItem> scheduleItemList, Integer scheduleVersion) {
        this.scheduleItemList = scheduleItemList;
        this.scheduleVersion = scheduleVersion;
    }

    public Integer getScheduleVersion() {
        return this.scheduleVersion;
    }

    public Dictionary<String, Day> getDays() {
        if (scheduleItemList == null)
            return null;

        Dictionary<String, Day> days = new Hashtable<String,Day>();
        Dictionary<Integer, List<ScheduleItem>> scheduleItemSorted = new Hashtable<Integer, List<ScheduleItem>>();

        for (int i = 0; i < 7; ++i) {
            scheduleItemSorted.put(i, new ArrayList<ScheduleItem>());
        }

        for (var scheduleItem:
             scheduleItemList) {
            scheduleItemSorted.get(scheduleItem.getWeekDay()).add(scheduleItem);
        }

        for (int i = 0; i < 7; ++i) {
            days.put(Integer.toString(i), new Day(scheduleItemSorted.get(i)));
        }

        return days;
    }

    public class Day {
        private final List<ScheduleItem> scheduleItemList;
        public Day(List<ScheduleItem> scheduleItemList) {
            this.scheduleItemList = scheduleItemList;
        }

        public List<List<Lesson>> getLessons() {
            if (scheduleItemList.isEmpty()) return null;

            List<List<Lesson>> lessons = new ArrayList<List<Lesson>>();

            for (int i = 0; i < 10; ++i)
                lessons.add(new ArrayList<Lesson>());

            for (ScheduleItem scheduleItem:
                 scheduleItemList) {
                lessons.get(scheduleItem.getLessonOrder()).add(new Lesson(scheduleItem));
            }

            return lessons;
        }

        public class Lesson {
            private final ScheduleItem lesson;
            public Lesson(ScheduleItem lesson) {
                this.lesson = lesson;
            }

            public UUID getId() {
                return lesson.getId();
            }

            public Integer getWeek() {
                return lesson.getWeek();
            }

            public List<Integer> getExcludedWeeks() {
                return lesson.getExcludedWeeks();
            }

            public Integer getOrder() {
                return lesson.getLessonOrder();
            }

            public LessonTypes getType() {
                return lesson.getType();
            }

            public String getRoom() {
                return lesson.getRoom();
            }

            public Teacher getTeacher() {
                if (lesson.getTeacherId() == null) return null;
                return new Teacher(lesson.getTeacherId(), lesson.getTeacher().getFullName());
            }

            public Subject getSubject() {
                return new Subject(lesson.getSubjectId(), lesson.getSubject().getName());
            }

            public record Teacher(UUID id, String fullName) {
            }

            public record Subject(UUID id, String name) {
            }
        }
    }
}
