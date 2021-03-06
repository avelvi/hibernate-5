package com.avelvi.hibernate.chapter03.pojo;

public class Ranking {

    private Person subject;
    private Person observer;
    private Skill skill;
    private Integer ranking;

    public Ranking() {
    }

    public Person getSubject() {
        return subject;
    }

    public void setSubject(Person subject) {
        this.subject = subject;
    }

    public Person getObserver() {
        return observer;
    }

    public void setObserver(Person observer) {
        this.observer = observer;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Ranking{");
        sb.append("subject=").append(subject);
        sb.append(", observer=").append(observer);
        sb.append(", skill=").append(skill);
        sb.append(", ranking=").append(ranking);
        sb.append('}');
        return sb.toString();
    }
}
