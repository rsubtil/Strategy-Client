package effects;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.effects.EffectImpl;
import de.lessvoid.nifty.effects.EffectProperties;
import de.lessvoid.nifty.effects.Falloff;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.render.NiftyRenderEngine;
import java.util.ArrayList;

/* This effect changes the size of an element and
 * all it's children.
 *
 */

public class Size implements EffectImpl {
    
    private ArrayList<Element> elements;
    
    private int oldWidth;
    private int oldHeight;
    
    private float newWidth; 
    private float newHeight;

    @Override
    public void activate(Nifty nifty, Element elmnt, EffectProperties ep) {
        elements = new ArrayList<>();
        for(Element e : elmnt.getChildren()) {
            elements.add(e);
        }
        elements.add(elmnt);
        oldWidth = elmnt.getWidth();
        oldHeight = elmnt.getHeight();
        this.newWidth = Float.parseFloat(ep.getProperty("size", "2.0")) * oldWidth;
        this.newHeight = Float.parseFloat(ep.getProperty("size", "2.0")) * oldHeight;
    }

    @Override
    public void execute(Element elmnt, float f, Falloff flf, NiftyRenderEngine nre) {
        for(Element e : elements) {
            e.setWidth((int)newWidth);
            e.setHeight((int)newHeight);
        }
    }

    @Override
    public void deactivate() {
        for(Element e : elements) {
            e.setWidth(oldWidth);
            e.setHeight(oldHeight);
        }
    }
}
