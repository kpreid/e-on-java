package org.erights.e.ui.awt;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;


/**
 * Presents a simple to use abstraction of some of the functionality of
 * GridBagLayout
 *
 * @author Mark S. Miller
 */
public class BrickLayer extends GridBagLayout {

    /**
     *
     */
    public BrickLayer() {
        this(GridBagConstraints.BOTH);
    }

    /**
     *
     */
    public BrickLayer(int fill) {
        super();
        defaultConstraints.fill = fill;
        defaultConstraints.weightx = 0.00001;
        defaultConstraints.weighty = 0.00001;
    }

    /**
     *
     */
    public void addLayoutComponent(Component comp, Object constraints) {
        if (constraints == null) {
            setConstraints(comp, defaultConstraints);
        } else if (constraints instanceof String) {
            addLayoutComponent((String)constraints, comp);
        } else {
            super.addLayoutComponent(comp, constraints);
        }
    }

    /**
     *
     */
    public void addLayoutComponent(String name, Component comp) {
        GridBagConstraints gbc =
          (GridBagConstraints)defaultConstraints.clone();
        if (name.indexOf('x') != -1) {
            gbc.weightx = 1.0;
        }
        if (name.indexOf('y') != -1) {
            gbc.weighty = 1.0;
        }
        if (name.indexOf('\n') != -1) {
            gbc.gridwidth = GridBagConstraints.REMAINDER;
        }
        setConstraints(comp, gbc);
    }
}
