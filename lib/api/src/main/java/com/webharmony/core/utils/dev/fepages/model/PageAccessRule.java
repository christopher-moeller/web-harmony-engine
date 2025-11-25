package com.webharmony.core.utils.dev.fepages.model;

import com.webharmony.core.configuration.security.ApplicationRight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface PageAccessRule {

    List<ApplicationRight> getRights();

    boolean isPublic();

    boolean isAndConnected();

    static PageAccessRule publicAccess() {
        return new PublicAccessRule();
    }

    static PageAccessRule requiresRight(ApplicationRight applicationRight) {
        return new ApplicationRightAccessRule(applicationRight);
    }

    static PageAccessRule requiresRightAtLeastOneRight(ApplicationRight... rights) {
        return new AtLeastOneApplicationRightAccessRule(List.of(rights));
    }

    static PageAccessRule collectFromChildren() {
        return new CollectFromChildrenAccessRule();
    }

    class PublicAccessRule implements PageAccessRule {

        @Override
        public List<ApplicationRight> getRights() {
            return Collections.emptyList();
        }

        @Override
        public boolean isPublic() {
            return true;
        }

        @Override
        public boolean isAndConnected() {
            return true;
        }
    }

    class ApplicationRightAccessRule implements PageAccessRule {

        private final ApplicationRight applicationRight;

        public ApplicationRightAccessRule(ApplicationRight applicationRight) {
            this.applicationRight = applicationRight;
        }

        @Override
        public List<ApplicationRight> getRights() {
            return Collections.singletonList(applicationRight);
        }

        @Override
        public boolean isPublic() {
            return false;
        }

        @Override
        public boolean isAndConnected() {
            return false;
        }
    }

    @SuppressWarnings("all")
    class AtLeastOneApplicationRightAccessRule implements PageAccessRule {

        private final List<ApplicationRight> rights;

        public AtLeastOneApplicationRightAccessRule(List<ApplicationRight> rights) {
            this.rights = rights;
        }

        @Override
        public List<ApplicationRight> getRights() {
            return rights;
        }

        @Override
        public boolean isPublic() {
            return false;
        }

        @Override
        public boolean isAndConnected() {
            return false;
        }
    }

    class CollectFromChildrenAccessRule implements PageAccessRule {

        private List<ApplicationRight> rights;

        public void init(List<Page> children) {
            this.rights = null;
            final List<ApplicationRight> resultList = new ArrayList<>();
            for (Page child : children) {
                for (ApplicationRight right : child.getAccessRuleConfig().getRights()) {
                    if(!resultList.contains(right))
                        resultList.add(right);
                }
            }
            this.rights = resultList;
        }
        @Override
        public List<ApplicationRight> getRights() {
            if(rights == null)
                throw new IllegalStateException("init() method was not called");
            return rights;
        }

        @Override
        public boolean isPublic() {
            return false;
        }

        @Override
        public boolean isAndConnected() {
            return false;
        }
    }

}
