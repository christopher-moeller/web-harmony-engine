package com.webharmony.starter.builder;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VirtualDir {

    @Getter
    private final String name;

    @Getter
    @Setter
    private VirtualDir parent;

    @Getter
    private final List<VirtualDir> children = new ArrayList<>();

    @Getter
    private final List<VirtualFile> files = new ArrayList<>();

    public VirtualDir(String name) {
        this.name = name;
    }

    public void addChild(VirtualDir child) {
        child.setParent(this);
        children.add(child);
    }

    public void addFile(VirtualFile file) {
        file.setParent(this);
        files.add(file);
    }

    public Path getFullPath() {
        if (parent == null) {
            return Path.of(name);
        } else {
            return parent.getFullPath().resolve(name);
        }
    }

    public void printTree(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("  ");
        }
        sb.append(name);
        System.out.println(sb.toString());
        for (VirtualDir child : children) {
            child.printTree(depth + 1);
        }

        for (VirtualFile file : files) {
            StringBuilder fileSb = new StringBuilder();
            for (int i = 0; i < depth + 1; i++) {
                fileSb.append("  ");
            }
            fileSb.append(file.getName());
            System.out.println(fileSb.toString());
        }

    }
}
