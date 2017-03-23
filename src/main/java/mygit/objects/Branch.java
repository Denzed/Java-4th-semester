package mygit.objects;

import org.jetbrains.annotations.NotNull;

/**
 * Class which stores information about repository branches
 */
public class Branch {
    /**
     * String constant used to distinguish Branches from other MyGit objects
     */
    public static final String TYPE = "branch";

    @NotNull
    private final String name;

    /**
     * Constructs a branch with the given name
     * @param name name of the branch
     */
    public Branch(@NotNull String name) {
        this.name = name;
    }

    /**
     * Gets the name of the branch
     * @return name of the branch
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Generated method used to check equality between Branch and other objects
     * @param object object to check equality with
     * @return {@code true} if the given object is a {@link Branch} with the same name and {@code false} otherwise
     */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Branch branch = (Branch) object;
        return name.equals(branch.name);
    }

    /**
     * Generated method used to hash Branch objects
     * @return computed hash of the Branch
     */
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}