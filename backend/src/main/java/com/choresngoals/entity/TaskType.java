package com.choresngoals.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TaskType {
    @JsonProperty("daily")
    DAILY,

    @JsonProperty("weekly")
    WEEKLY,

    @JsonProperty("monthly")
    MONTHLY,

    @JsonProperty("one_time")
    ONE_TIME
}
