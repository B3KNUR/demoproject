import java.util.Comparator;
import java.util.List;

public interface Researcher {

    // Prints the research papers sorted using the given comparator
    //List<ResearchPaper> printPapers(Comparator<ResearchPaper> c);

    // Calculates the H-Index of the researcher
    int calculateHIndex();

    // Adds a research project to the researcher's list of projects
    void addResearchProject(ResearchProject project);

    // Retrieves the list of research projects
    List<ResearchProject> getResearchProjects();

    List<ResearchPaper> printPapers(Comparator<ResearchPaper> c);
}