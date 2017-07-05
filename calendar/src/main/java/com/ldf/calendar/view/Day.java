package com.ldf.calendar.view;

import android.os.Parcel;
import android.os.Parcelable;

import com.ldf.calendar.component.State;
import com.ldf.calendar.model.CalendarDate;

/**
 * Created by ldf on 17/7/5.
 */

public class Day implements Parcelable {
    private State state;
    private CalendarDate date;
    private int posRow;
    private int posCol;

    public Day(State state , CalendarDate date , int posRow , int posCol) {
        this.state = state;
        this.date = date;
        this.posRow = posRow;
        this.posCol = posCol;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public CalendarDate getDate() {
        return date;
    }

    public void setDate(CalendarDate date) {
        this.date = date;
    }

    public int getPosRow() {
        return posRow;
    }

    public void setPosRow(int posRow) {
        this.posRow = posRow;
    }

    public int getPosCol() {
        return posCol;
    }

    public void setPosCol(int posCol) {
        this.posCol = posCol;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.state == null ? -1 : this.state.ordinal());
        dest.writeSerializable(this.date);
        dest.writeInt(this.posRow);
        dest.writeInt(this.posCol);
    }

    protected Day(Parcel in) {
        int tmpState = in.readInt();
        this.state = tmpState == -1 ? null : State.values()[tmpState];
        this.date = (CalendarDate) in.readSerializable();
        this.posRow = in.readInt();
        this.posCol = in.readInt();
    }

    public static final Parcelable.Creator<Day> CREATOR = new Parcelable.Creator<Day>() {
        @Override
        public Day createFromParcel(Parcel source) {
            return new Day(source);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };
}
