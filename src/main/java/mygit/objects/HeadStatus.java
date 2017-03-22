package mygit.objects;

import org.jetbrains.annotations.NotNull;

/**
 * Stores information about repository HEAD status
 */
public class HeadStatus {
    @NotNull
    private String type;
    @NotNull
    private String name;

    /**
     * Constructs HeadStatus of a given type with a given name
     * @param type HEAD status type
     * @param name HEAD status name
     */
    public HeadStatus(@NotNull String type, @NotNull String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * Gets HEAD status type
     * @return HEAD status type
     */
    @NotNull
    public String getType() {
        return type;
    }

    /**
     * Gets HEAD status name
     * @return HEAD status name
     */
    @NotNull
    public String getName() {
        return name;
    }
}
