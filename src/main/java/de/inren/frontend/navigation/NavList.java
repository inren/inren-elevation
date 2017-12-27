/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.inren.frontend.navigation;


import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The "left side" menu. 
 * It was designed for that purpose, but maybe in the future we can use it for more. Who knows?
 * 
 * @author Ingo Renner
 *
 */
public class NavList extends Panel {
    
	private static final Logger log = LoggerFactory.getLogger(NavList.class);
	
    private GNode<NavigationElement> node;
    
    public NavList(String id, GNode<NavigationElement> node) {
        super(id);
        this.node = node;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
            initGui();
    }

    private void initGui() {
        
        List<GNode<NavigationElement>> list = node.getChildren();
        
        ListView<GNode<NavigationElement>> listview = new ListView<GNode<NavigationElement>>("nav-ul", list) {

            @Override
            protected void populateItem(ListItem<GNode<NavigationElement>> item) {
                
                NavigationElement data = item.getModel().getObject().getData();
                    item.add(new NavListItem<Page>("nav-li", data.getClazz(), 
                            new StringResourceModel(data.getLanguageKey(), getComponent(data), null)));
                if (item.getModel().getObject().getChildren().isEmpty()) {
                    item.add(new Label("nav-sub", "").setVisible(false));
                } else {
                    item.add(new NavList("nav-sub", item.getModel().getObject()));
                }
            }

        };
        add(listview);
    }
    
    protected Page getComponent(NavigationElement data) {
        try {
            return data.getClazz().newInstance();
        } catch (InstantiationException e) {
			log.error(e.getMessage(),e);
            throw new RuntimeException(e.getMessage());
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(),e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
