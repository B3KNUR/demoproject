import java.util.List;
import java.util.ArrayList;

class ResearchProject {
    private String topic;
    private List<Researcher> participants;
    private List<ResearchPaper> publishedPapers;

    public ResearchProject(String topic) {
        this.topic = topic;
        this.participants = new ArrayList<>();
        this.publishedPapers = new ArrayList<>();
    }

    public void addParticipant(Researcher researcher) {
        participants.add(researcher);
    }

    public void addPaper(ResearchPaper paper) {
        publishedPapers.add(paper);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<Researcher> getParticipants() {
        return participants;
    }

    public List<ResearchPaper> getPublishedPapers() {
        return publishedPapers;
    }
}
