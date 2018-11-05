package weber.kaden.common.model;

import java.util.Objects;

public class Route {
    private City city1;
    private City city2;
    private Integer score;
    private Integer cost;
    private TrainCardType type;

    public Route(City city1, City city2, Integer cost, TrainCardType type) {
        this.city1 = city1;
        this.city2 = city2;
        switch (cost){
            case 1:
                score = 1;
                break;
            case 2:
                score = 2;
                break;
            case 3:
                score = 4;
                break;
            case 4:
                score = 7;
                break;
            case 5:
                score = 10;
                break;
            case 6:
                score = 15;
                break;
            default:
                score = 0;
        }
        this.cost = cost;
        this.type = type;
    }

    public City getCity1() {
        return city1;
    }

    public void setCity1(City city1) {
        this.city1 = city1;
    }

    public City getCity2() {
        return city2;
    }

    public void setCity2(City city2) {
        this.city2 = city2;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public TrainCardType getType() {
        return type;
    }

    public void setType(TrainCardType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route route = (Route) o;
        return city1 == route.city1 &&
                city2 == route.city2 &&
                Objects.equals(score, route.score) &&
                Objects.equals(cost, route.cost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city1, city2, score, cost);
    }
}