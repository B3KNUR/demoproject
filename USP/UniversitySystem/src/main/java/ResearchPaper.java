import java.util.Objects;
import java.util.Date;
import java.util.List;


class ResearchPaper {
    private String title;
    private List<String> authors;
    private String journal;
    private int citations;
    private String pages;
    private String doi;
    private Date publicationDate;
    private Format format;

    public ResearchPaper(String title, List<String> authors, String journal, int citations, String pages, String doi, Date publicationDate) {
        this.title = title;
        this.authors = authors;
        this.journal = journal;
        this.citations = citations;
        this.pages = pages;
        this.doi = doi;
        this.publicationDate = publicationDate;
    }

    //public getcitations

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ResearchPaper that = (ResearchPaper) obj;
        return Objects.equals(doi, that.doi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doi);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public int getCitations() {
        return citations;
    }

    public void setCitations(int citations) {
        this.citations = citations;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }
}
