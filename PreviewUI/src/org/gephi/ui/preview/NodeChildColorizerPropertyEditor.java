package org.gephi.ui.preview;

import org.gephi.preview.api.color.colorizer.Colorizer;
import org.gephi.preview.api.color.colorizer.ColorizerFactory;
import org.gephi.preview.api.color.colorizer.ColorizerType;
import org.openide.util.Lookup;

/**
 *
 * @author jeremy
 */
public class NodeChildColorizerPropertyEditor extends AbstractColorizerPropertyEditor {

    protected void setSupportedColorizerTypes() {
        addSupportedColorizerType(ColorizerType.CUSTOM);
        addSupportedColorizerType(ColorizerType.PARENT_NODE);
    }

    protected Colorizer createColorizer(ColorizerType type) {
        ColorizerFactory f = Lookup.getDefault().lookup(ColorizerFactory.class);
        return f.createNodeChildColorizer(type);
    }
}
