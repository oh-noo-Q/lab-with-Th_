package entities.solverhelper;

/**
 * path constraint: "trie[0].root_node != NULL" ---> new variable: "trie[0].root_node",  "trie", "trie[0]"
 */
public class NewVariableInSe {
    private String originalName; // come from the constraint
    private String normalizedName; // used in smt-lib

    public NewVariableInSe(String originalName, String normalizedName){
        this.originalName = originalName;
        this.normalizedName = normalizedName;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setNormalizedName(String normalizedName) {
        this.normalizedName = normalizedName;
    }

    @Override
    public String toString() {
        return String.format("original: \"%s\"; modified: \"%s\"\n", originalName, normalizedName);
    }
}
