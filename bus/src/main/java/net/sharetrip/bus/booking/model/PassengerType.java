package net.sharetrip.bus.booking.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({PassengerType.PRIMARY_ADULT, PassengerType.SECONDARY_ADULT, PassengerType.CHILDREN, PassengerType.INFANT})
@Retention(RetentionPolicy.SOURCE)
public @interface PassengerType {
    int PRIMARY_ADULT = 1;
    int SECONDARY_ADULT = 2;
    int CHILDREN = 3;
    int INFANT = 4;
}
