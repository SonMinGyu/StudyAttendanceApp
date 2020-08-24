package com.application.studyattendance.model;

public class StudyModel {
    public String studyHostUid;
    public String studyName;
    public String studyCategory;
    public int studyTotalNumber;
    public int offOrOn; // off만 체크 = 0, on만 체크 = 1, off,on 둘다체크 = 2
    public boolean notOpenOrOpen; // 비공개 스터디체크 했으면 studyModel의 notOpenorOpen이 true, 공개 체크했으면 false;
    public boolean selfOrFixed; // 자율 스터디체크 했으면 studyModel의 selfOrFixed가 true, 공개 체크했으면 false;
    public boolean isMission;
    public String missionText;
    public boolean isPush;
    public String profile;
    public String studyKey;
    public boolean place_nowOrLater; // now = true, later = false
    public String place_name;
    public String place_address;
    public String place_area;

    public String getPlace_area() {
        return place_area;
    }

    public void setPlace_area(String place_area) {
        this.place_area = place_area;
    }

    public boolean isPlace_nowOrLater() {
        return place_nowOrLater;
    }

    public void setPlace_nowOrLater(boolean place_nowOrLater) {
        this.place_nowOrLater = place_nowOrLater;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getPlace_address() {
        return place_address;
    }

    public void setPlace_address(String place_address) {
        this.place_address = place_address;
    }

    public String getMissionText() {
        return missionText;
    }

    public void setMissionText(String missionText) {
        this.missionText = missionText;
    }

    public String getStudyHostUid() {
        return studyHostUid;
    }

    public void setStudyHostUid(String studyHostUid) {
        this.studyHostUid = studyHostUid;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public String getStudyCategory() {
        return studyCategory;
    }

    public void setStudyCategory(String studyCategory) {
        this.studyCategory = studyCategory;
    }

    public int getStudyTotalNumber() {
        return studyTotalNumber;
    }

    public void setStudyTotalNumber(int studyTotalNumber) {
        this.studyTotalNumber = studyTotalNumber;
    }

    public int getOffOrOn() {
        return offOrOn;
    }

    public void setOffOrOn(int offOrOn) {
        this.offOrOn = offOrOn;
    }

    public boolean getIsNotOpenOrOpen() {
        return notOpenOrOpen;
    }

    public void setNotOpenOrOpen(boolean notOpenOrOpen) {
        this.notOpenOrOpen = notOpenOrOpen;
    }

    public boolean getIsSelfOrFixed() {
        return selfOrFixed;
    }

    public void setSelfOrFixed(boolean selfOrFixed) {
        this.selfOrFixed = selfOrFixed;
    }

    public boolean getIsMission() {
        return isMission;
    }

    public void setMission(boolean mission) {
        this.isMission = mission;
    }

    public boolean getIsPush() {
        return isPush;
    }

    public void setPush(boolean push) {
        this.isPush = push;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getStudyKey() {
        return studyKey;
    }

    public void setStudyKey(String studyKey) {
        this.studyKey = studyKey;
    }
}
