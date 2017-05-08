package ru.spbau.daniil.smirnov.mygit.objects;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class which stores a structure of a directory. May contain either blobs or subtrees.
 */
public class Tree implements Serializable {
    /**
     * String constant used to distinguish Trees from other MyGit objects
     */
    public static final String TYPE = "tree";

    @NotNull
    private final List<TreeEdge> edgesToChildren;

    /**
     * Class which represents edges in a directory graph
     */
    public static class TreeEdge implements Serializable {
        @NotNull
        private String hash;

        @NotNull
        private String name;

        @NotNull
        private String type;

        @NotNull
        private Date creationDate;

        private TreeEdge(@NotNull String hash,
                         @NotNull String name,
                         @NotNull String type,
                         @NotNull Date creationDate) {
            this.hash = hash;
            this.name = name;
            this.type = type;
            this.creationDate = creationDate;
        }

        /**
         * Constructs an object with the given name, hash and type. Current date will be used as a date of creation
         * @param hash hash of the object, to which the edge is leading
         * @param name name of the object, to which the edge is leading
         * @param type type of the object, to which the edge is leading
         */
        public TreeEdge(@NotNull String hash,
                        @NotNull String name,
                        @NotNull String type) {
            this(hash, name, type, new Date());
        }

        /**
         * Gets object hash
         * @return object hash
         */
        @NotNull
        public String getHash() {
            return hash;
        }

        /**
         * Gets object name
         * @return object name
         */
        @NotNull
        public String getName() {
            return name;
        }

        /**
         * Gets object type
         * @return object type
         */
        @NotNull
        public String getType() {
            return type;
        }

        /**
         * Gets object creation date
         * @return object creation date
         */
        @NotNull
        public Date getCreationDate() {
            return creationDate;
        }

        /**
         * Checks whether current object represents a directory or not
         * @return {@code true} if it is a directory and {@code false} otherwise
         */
        public boolean isDirectory() {
            return getType().equals(Tree.TYPE);
        }
    }

    private Tree(@NotNull List<TreeEdge> edgesToChildren) {
        this.edgesToChildren = edgesToChildren;
    }

    /**
     * Construct an empty Tree
     */
    public Tree() {
        this(new ArrayList<>());
    }

    /**
     * Adds a new edge to a child to the list of edges
     * @param edgeToChild adds {@link TreeEdge} as a new child of the tree
     * @return whether it was actually added
     */
    public boolean addEdgeToChild(TreeEdge edgeToChild) {
        return edgesToChildren.add(edgeToChild);
    }

    /**
     * Gets the {@link List} of edges to children of a current tree
     * @return {@link List} of edges to children
     */
    @NotNull
    public List<TreeEdge> getEdgesToChildren() {
        return edgesToChildren;
    }
}