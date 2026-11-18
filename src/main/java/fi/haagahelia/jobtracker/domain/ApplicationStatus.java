package fi.haagahelia.jobtracker.domain;
// THE STEPS OF ONE JOB APPLICATION

// ENUM ALLOWS ONLY THESE VALUES, SO NOBODY CAN TYPE A WRONG STATUS

public enum ApplicationStatus {
    WISHLIST,
    APPLIED,
    INTERVIEW,
    OFFER,
    REJECTED;
}
