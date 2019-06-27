import java.util.Date;

public class Schedule {

    private int id;
    private String title;
    private String desc;
    private String cat;
    private int priority;
    private Date dead;

    public Schedule(){}
    public Schedule(int id, String title, String desc, String cat, int priority, Date dead)
    {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.cat = cat;
        this.priority = priority;
        this.dead = dead;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }

    public String getCat() { return cat; }
    public void setCat(String Cat) { this.cat = cat; }

    public Date getDead() { return dead; }
    public void setDead(Date ded) { this.dead = dead; }

    public int getPrio() { return priority; }
    public void setPrio(Byte Prio) { this.priority = priority; }
}
