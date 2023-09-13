public enum Rank {
    BEGINNER("Beginner", 0.00),
    GOOD_START("Good Start", 0.02),
    MOVING_UP("Moving Up", 0.05),
    GOOD("Good", 0.08),
    SOLID("Solid", 0.15),
    NICE("Nice", 0.25),
    GREAT("Great", 0.40),
    AMAZING("Amazing", 0.50),
    GENIUS("Genius", 0.70),
    QUEEN_BEE("Queen Bee", 1.00);

    /** The localized String version of the rank name. */
    private String rankName;
    /** The percentage of total points needed to achive the rank. */
    private double requiredPercent;

    /**
     * Gets the String localized name of the rank.
     * 
     * @return The name of the rank
     */
    public String getRankName() {
        return rankName;
    }

    /**
     * Gets the number of points needed to achive the rank given the total
     * number of possible point.
     * 
     * @param totalPoints The total number of possible points that can be earned
     *                    in a puzzle
     * @return The number of points needed to achive the rank
     */
    public int getRequiredPoints(int totalPoints) {
        return (int) Math.round(totalPoints * requiredPercent);
    }

    Rank(String rankName, double requiredPercent) {
        this.rankName = rankName;
        this.requiredPercent = requiredPercent;
    }
}
