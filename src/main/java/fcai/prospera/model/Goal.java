package fcai.prospera.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class Goal {
    private UUID id;
    private UUID userId;
    private String name;
    private GoalType type;
    private BigDecimal targetAmount;
    private Date deadline;
    private BigDecimal progress;
    private boolean autoDeductFromIncome;
    private BigDecimal monthlyContribution;
    private GoalStatus status;

    public Goal() {
        this.id = UUID.randomUUID();
        this.status = GoalStatus.ACTIVE;
        this.progress = BigDecimal.ZERO;
    }

    public Goal(UUID userId, String name, GoalType type, BigDecimal targetAmount,
                Date deadline, boolean autoDeductFromIncome, BigDecimal monthlyContribution) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.targetAmount = targetAmount;
        this.deadline = deadline;
        this.progress = BigDecimal.ZERO;
        this.autoDeductFromIncome = autoDeductFromIncome;
        this.monthlyContribution = monthlyContribution;
        this.status = GoalStatus.ACTIVE;
    }

    public BigDecimal getMonthlyRateRequired() {
        if (deadline == null) {
            return BigDecimal.ZERO;
        }

        // Calculate months remaining
        long currentTimeMillis = System.currentTimeMillis();
        long deadlineMillis = deadline.getTime();
        long diffMillis = deadlineMillis - currentTimeMillis;

        if (diffMillis <= 0) {
            return BigDecimal.ZERO; // Past deadline
        }

        // Convert milliseconds to months (approximate)
        int monthsRemaining = (int) (diffMillis / (1000L * 60 * 60 * 24 * 30));

        if (monthsRemaining == 0) {
            return targetAmount.subtract(progress); // Due this month
        }

        // Calculate required monthly contribution
        BigDecimal remaining = targetAmount.subtract(progress);
        return remaining.divide(new BigDecimal(monthsRemaining), 2, BigDecimal.ROUND_CEILING);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GoalType getType() {
        return type;
    }

    public void setType(GoalType type) {
        this.type = type;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public BigDecimal getProgress() {
        return progress;
    }

    public void setProgress(BigDecimal progress) {
        this.progress = progress;

        // Update status if goal is completed
        if (progress.compareTo(targetAmount) >= 0) {
            this.status = GoalStatus.COMPLETED;
        }
    }

    public boolean isAutoDeductFromIncome() {
        return autoDeductFromIncome;
    }

    public void setAutoDeductFromIncome(boolean autoDeductFromIncome) {
        this.autoDeductFromIncome = autoDeductFromIncome;
    }

    public BigDecimal getMonthlyContribution() {
        return monthlyContribution;
    }

    public void setMonthlyContribution(BigDecimal monthlyContribution) {
        this.monthlyContribution = monthlyContribution;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Goal{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", targetAmount=" + targetAmount +
                ", deadline=" + deadline +
                ", progress=" + progress +
                ", status=" + status +
                '}';
    }
}