package net.sharetrip.flight.history.model;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

@Retention(SOURCE)
@IntDef({RuleType.AIR_FARE_RULE, RuleType.BAGGAGE, RuleType.FARE_DETAILS})
public @interface RuleType {
    int AIR_FARE_RULE = 1;
    int BAGGAGE = 2;
    int FARE_DETAILS = 3;
}
