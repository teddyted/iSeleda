/*
 * $Id: BasicCategoryUI.java 3475 2009-08-28 08:30:47Z kleopatra $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jdesktop.swingx.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Graphics;

import java.awt.Image.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.imageio.ImageIO.*;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.html.HTMLDocument;

import org.jdesktop.swingx.JXCategory;
import org.jdesktop.swingx.SwingXUtilities;
import org.jdesktop.swingx.JXCategory.ShowOnStartupChoice;
import org.jdesktop.swingx.plaf.CategoryUI;
import org.jdesktop.swingx.plaf.UIManagerExt;
import org.jdesktop.swingx.tips.CategoryModel.Category;

/**
 * Base implementation of the <code>JXCategory</code> UI.
 * 
 * @author <a href="mailto:fred@L2FProd.com">Frederic Lavigne</a>
 */
public class BasicCategoryUI extends CategoryUI {

  public static ComponentUI createUI(JComponent c) {
    return new BasicCategoryUI((JXCategory)c);
  }

  protected JXCategory tipPane;
  protected JPanel tipArea;
  protected Component currentCategoryComponent;

  protected Font tipFont;
  protected PropertyChangeListener changeListener;

  //private static final String BACKGROUND_IMAGE = "resources/dictionary_categories.png";
  
  public BasicCategoryUI(JXCategory tipPane) {
    this.tipPane = tipPane;
  }

  //private BufferedImage image;
  
  @Override
  public JDialog createDialog(Component parentComponent,
    final ShowOnStartupChoice choice) {
      //BufferedImage myImg = null;
   //   try {
          // return createDialog(parentComponent, choice, true);
          //super.paint(new Graphics g);
          //string BACKGROUND_IMAGE = "resources/dictionary_categories.png";
          //InputStream imgStream =
          
          
      ////try {
          //BufferedImage image = ImageIO.read(img); //("resources/dictionary_categories.png")); src/main/resources/org/jdesktop/swingx/plaf/basic/resources/
          //URL imgURL32 = getClass().getResource("/images/pixelitor_icon32.png");
          
          ////BufferedImage img = new BufferedImage(500,500, BufferedImage.TYPE_INT_ARGB);
          ////File f;
          ////f = new File("src/main/resources/images/dictionary_categories.png");
          ////ImageIO.write(img, "PNG", f);
      ////} catch (IOException ex) {
        ////       Logger.getLogger(BasicCategoryUI.class.getName()).log(Level.SEVERE, null, ex);
      ////}

    //JLabel label = new JLabel(new ImageIcon("/dictionary_categories.png"));
    JLabel label;
    // getContentpane().setLayout(new BorderLayout());
    // getContentpane().add(label);
      label = new JLabel(new ImageIcon("/Users/teddyted/NetBeansProjects/pixelitor_3.1.3_src/src/main/resources/images/dictionary_categories.png"));
    
    
    JOptionPane optionPane = new JOptionPane();
    optionPane.setLayout(new BorderLayout());
    //optionPane.add(label);
    optionPane.setMessage("MSL Categories");
    //optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
    optionPane.setOptions(new Object[] {new JButton("Button")});
    
    
    JDialog dialog = optionPane.createDialog(null, "MSL Categories");
    
//    dialog.setIconImage(myImg);
    dialog.setSize(0x304, 0x208);
    dialog.add(label);
    // dialog.setVisible(true);
    return dialog;
  }
  
  protected JDialog createDialog(Component parentComponent,
    final ShowOnStartupChoice choice,
    boolean showPreviousButton) {
    Locale locale = parentComponent==null ? null : parentComponent.getLocale();
    String title = UIManagerExt.getString("Category.dialogTitle", locale);

    final JDialog dialog;

    Window window;
    if (parentComponent == null) {
      window = JOptionPane.getRootFrame();
    } else {
      window = (parentComponent instanceof Window)?(Window)parentComponent
        :SwingUtilities.getWindowAncestor(parentComponent);
    }

    if (window instanceof Frame) {
      dialog = new JDialog((Frame)window, title, true);
    } else {
      dialog = new JDialog((Dialog)window, title, true);
    }

    dialog.getContentPane().setLayout(new BorderLayout(10, 10));
    dialog.getContentPane().add(tipPane, BorderLayout.CENTER);
    ((JComponent)dialog.getContentPane()).setBorder(BorderFactory
      .createEmptyBorder(10, 10, 10, 10));

    final JCheckBox showOnStartupBox;

    // tip controls
    JPanel controls = new JPanel(new BorderLayout());
    dialog.add("South", controls);

    if (choice != null) {
      showOnStartupBox = new JCheckBox(UIManagerExt
        .getString("Category.showOnStartupText", locale), choice
        .isShowingOnStartup());
      controls.add(showOnStartupBox, BorderLayout.CENTER);
    } else {
      showOnStartupBox = null;
    }

    JPanel buttons =
      new JPanel(new GridLayout(1, showPreviousButton?3:2, 9, 0));
    controls.add(buttons, BorderLayout.LINE_END);
    
    if (showPreviousButton) {
      JButton previousCategoryButton = new JButton(UIManagerExt
        .getString("Category.previousCategoryText", locale));
      buttons.add(previousCategoryButton);
      previousCategoryButton.addActionListener(getActionMap().get("previousCategory"));
    }
    
    JButton nextCategoryButton = new JButton(UIManagerExt
      .getString("Category.nextCategoryText", locale));
    buttons.add(nextCategoryButton);
    nextCategoryButton.addActionListener(getActionMap().get("nextCategory"));
    
    JButton closeButton = new JButton(UIManagerExt
      .getString("Category.closeText", locale));
    buttons.add(closeButton);
    
    final ActionListener saveChoice = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (choice != null) {
          choice.setShowingOnStartup(showOnStartupBox.isSelected());
        }
        dialog.setVisible(false);
      }
    };

    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {        
        dialog.setVisible(false);
        saveChoice.actionPerformed(null);
      }
    });
    dialog.getRootPane().setDefaultButton(closeButton);
    
    dialog.addWindowListener(new WindowAdapter() {
      @Override
    public void windowClosing(WindowEvent e) {
        saveChoice.actionPerformed(null);
      }
    });
    
    ((JComponent)dialog.getContentPane()).registerKeyboardAction(saveChoice,
      KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
      JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    dialog.pack();
    dialog.setLocationRelativeTo(parentComponent);

    return dialog;
  }

  @Override
  public void installUI(JComponent c) {
    super.installUI(c);
    installDefaults();
    installKeyboardActions();
    installComponents();
    installListeners();

    showCurrentCategory();
  }

  protected void installKeyboardActions() {
    ActionMap map = getActionMap();
    if (map != null) {
      SwingUtilities.replaceUIActionMap(tipPane, map);
    }
  }

  ActionMap getActionMap() {
    ActionMap map = new ActionMapUIResource();
    map.put("previousCategory", new PreviousCategoryAction());
    map.put("nextCategory", new NextCategoryAction());
    return map;
  }

  protected void installListeners() {
    changeListener = createChangeListener();
    tipPane.addPropertyChangeListener(changeListener);
  }

  protected PropertyChangeListener createChangeListener() {
    return new ChangeListener();
  }

  protected void installDefaults() {
    LookAndFeel.installColorsAndFont(tipPane, "Category.background",
      "Category.foreground", "Category.font");
    LookAndFeel.installBorder(tipPane, "Category.border");
    LookAndFeel.installProperty(tipPane, "opaque", Boolean.TRUE);
    tipFont = UIManager.getFont("Category.tipFont");
  }

  protected void installComponents() {
    tipPane.setLayout(new BorderLayout());

    // tip icon
    JLabel tipIcon = new JLabel(UIManagerExt
      .getString("Category.didYouKnowText", tipPane.getLocale()));
    tipIcon.setIcon(UIManager.getIcon("Category.icon"));
    tipIcon.setBorder(BorderFactory.createEmptyBorder(22, 15, 22, 15));
    tipPane.add("North", tipIcon);

    // tip area
    tipArea = new JPanel(new BorderLayout(2, 2));
    tipArea.setOpaque(false);
    tipArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    tipPane.add("Center", tipArea);
  }

  @Override
  public Dimension getPreferredSize(JComponent c) {
    return new Dimension(420, 175);
  }

  protected void showCurrentCategory() {
    if (currentCategoryComponent != null) {
      tipArea.remove(currentCategoryComponent);
    }

    int currentCategory = tipPane.getCurrentCategory();
    if (currentCategory == -1) {
      JLabel label = new JLabel();
      label.setOpaque(true);
      label.setBackground(UIManager.getColor("TextArea.background"));
      currentCategoryComponent = label;
      tipArea.add("Center", currentCategoryComponent);
      return;
    }

    // tip does not fall in current tip range
    if (tipPane.getModel() == null || tipPane.getModel().getCategoryCount() == 0
      || (currentCategory < 0 && currentCategory >= tipPane.getModel().getCategoryCount())) {
      currentCategoryComponent = new JLabel();
    } else {    
      Category tip = tipPane.getModel().getCategoryAt(currentCategory);

      Object tipObject = tip.getCategory();
      if (tipObject instanceof Component) {
        currentCategoryComponent = (Component)tipObject;
      } else if (tipObject instanceof Icon) {
        currentCategoryComponent = new JLabel((Icon)tipObject);
      } else {
        JScrollPane tipScroll = new JScrollPane();
        tipScroll.setBorder(null);
        tipScroll.setOpaque(false);
        tipScroll.getViewport().setOpaque(false);
        tipScroll.setBorder(null);

        String text = tipObject == null?"":tipObject.toString();

        if (BasicHTML.isHTMLString(text)) {
          JEditorPane editor = new JEditorPane("text/html", text);
          editor.setFont(tipPane.getFont());
          BasicHTML.updateRenderer(editor, text);
          SwingXUtilities.setHtmlFont(
                  (HTMLDocument) editor.getDocument(), tipPane.getFont());
          editor.setEditable(false);
          editor.setBorder(null);
          editor.setMargin(null);
          editor.setOpaque(false);
          tipScroll.getViewport().setView(editor);
        } else {
          JTextArea area = new JTextArea(text);
          area.setFont(tipPane.getFont());
          area.setEditable(false);
          area.setLineWrap(true);
          area.setWrapStyleWord(true);
          area.setBorder(null);
          area.setMargin(null);
          area.setOpaque(false);
          tipScroll.getViewport().setView(area);
        }

        currentCategoryComponent = tipScroll;
      }
    }
    
    tipArea.add("Center", currentCategoryComponent);
    tipArea.revalidate();
    tipArea.repaint();
  }

  @Override
  public void uninstallUI(JComponent c) {
    uninstallListeners();
    uninstallComponents();
    uninstallDefaults();
    super.uninstallUI(c);
  }

  protected void uninstallListeners() {
    tipPane.removePropertyChangeListener(changeListener);
  }

  protected void uninstallComponents() {}

  protected void uninstallDefaults() {}

  class ChangeListener implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent evt) {
      if (JXCategory.CURRENT_TIP_CHANGED_KEY.equals(evt.getPropertyName())) {
        showCurrentCategory();
      }
    }
  }

  class PreviousCategoryAction extends AbstractAction {
    public PreviousCategoryAction() {
      super("previousCategory");
    }
    public void actionPerformed(ActionEvent e) {
      tipPane.previousCategory();
    }
    @Override
    public boolean isEnabled() {
      return tipPane.isEnabled();
    }
  }

  class NextCategoryAction extends AbstractAction {
    public NextCategoryAction() {
      super("nextCategory");
    }
    public void actionPerformed(ActionEvent e) {
      tipPane.nextCategory();
    }
    @Override
    public boolean isEnabled() {
      return tipPane.isEnabled();
    }
  }
  
}
