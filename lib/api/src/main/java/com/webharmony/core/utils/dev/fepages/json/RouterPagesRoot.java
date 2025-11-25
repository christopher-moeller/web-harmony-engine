package com.webharmony.core.utils.dev.fepages.json;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RouterPagesRoot {

    private List<PageJson> pages;
    private NavigationTreeJson navigationTree;

}
