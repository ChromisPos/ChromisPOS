/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.util;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

public class AutoCompleteComboBox extends PlainDocument {

    private JComboBox comboBox;
    private ComboBoxModel model;
    private JTextComponent editor;
    // flag to indicate if setSelectedItem has been called
    // subsequent calls to remove/insertString should be ignored
    private boolean selecting = false;
    private boolean hidePopupOnFocusLoss;
    private boolean hitBackspace = false;
    private boolean hitBackspaceOnSelection;

    private KeyListener editorKeyListener;
    private FocusListener editorFocusListener;

    public AutoCompleteComboBox(final JComboBox comboBox) {
        this.comboBox = comboBox;
        model = comboBox.getModel();
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!selecting) {
                    highlightCompletedText(0);
                }
            }
        });
        comboBox.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals("editor")) {                    
                    configureEditor((ComboBoxEditor) e.getNewValue());
                }
                if (e.getPropertyName().equals("model")) {                 
                    model = (ComboBoxModel) e.getNewValue();
                }
            }
        });
        editorKeyListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (comboBox.isDisplayable()) {
                    comboBox.setPopupVisible(true);
                }
                hitBackspace = false;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_BACK_SPACE:
                        hitBackspace = true;
                        hitBackspaceOnSelection = editor.getSelectionStart() != editor.getSelectionEnd();
                        break;
                    case KeyEvent.VK_DELETE:
                        e.consume();
                        Toolkit.getDefaultToolkit().beep();
                        break;
                }
            }
        };

        editorFocusListener = new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                highlightCompletedText(0);
            }
        };

        configureEditor(comboBox.getEditor());
        Object selected = comboBox.getSelectedItem();
        if (selected != null) {
            setText(selected.toString());
        }
        highlightCompletedText(0);
    }

    public static void enable(JComboBox comboBox) {
        comboBox.setEditable(true);
        new AutoCompleteComboBox(comboBox);
    }

    void configureEditor(ComboBoxEditor newEditor) {
        if (editor != null) {
            editor.removeKeyListener(editorKeyListener);
            editor.removeFocusListener(editorFocusListener);
        }

        if (newEditor != null) {
            editor = (JTextComponent) newEditor.getEditorComponent();
            editor.addKeyListener(editorKeyListener);
            editor.addFocusListener(editorFocusListener);
            editor.setDocument(this);
        }
    }

    public void remove(int offSet, int len) throws BadLocationException {
        if (selecting) {
            return;
        }
        if (hitBackspace) {
            switch (offSet) {
                case 0:
                    setText("");
                    model.setSelectedItem(null);
                    break;
                default:
                    if (hitBackspaceOnSelection) {
                        offSet--;
                        if (offSet == 0) {
                            setText("");
                            model.setSelectedItem(null);
                        }
                    }
                    break;
            }
            highlightCompletedText(offSet);
        } else {
            super.remove(offSet, len);
        }
    }

    public void insertString(int offSet, String str, AttributeSet a) throws BadLocationException {
        if (selecting) {
            return;
        }
        super.insertString(offSet, str, a);
        Object item = lookupItem(getText(0, getLength()));
        if (item != null) {
            setSelectedItem(item);
        } else {
            item = comboBox.getSelectedItem();
            offSet = offSet - str.length();// - 1; 
            if (offSet < 0) {
                offSet = 0;
            }
            Toolkit.getDefaultToolkit().beep();
        }
        if (item != null) {
            setText(item.toString());
            highlightCompletedText(offSet + str.length());
        } else {
            super.remove(0, getLength());
        }
    }

    private void setText(String text) {
        try {
            // remove all text and insert the completed string
            super.remove(0, getLength());
            super.insertString(0, text, null);
        } catch (BadLocationException e) {
            throw new RuntimeException(e.toString());
        }
    }

    private void highlightCompletedText(int start) {
        editor.setCaretPosition(getLength());
        editor.moveCaretPosition(start);
    }

    private void setSelectedItem(Object item) {
        selecting = true;
        model.setSelectedItem(item);
        selecting = false;
    }

    private Object lookupItem(String pattern) {
        Object selectedItem = model.getSelectedItem();
        // only search for a different item if the currently selected does not match
        if (selectedItem != null && startsWithIgnoreCase(selectedItem.toString(), pattern)) {
            return selectedItem;
        } else {
            for (int i = 0, n = model.getSize(); i < n; i++) {
                Object currentItem = model.getElementAt(i);
                // current item starts with the pattern?
                if (currentItem != null && startsWithIgnoreCase(currentItem.toString(), pattern)) {
                    return currentItem;
                }
            }
        }
        return null;
    }

    // checks if str1 starts with str2 - ignores case
    private boolean startsWithIgnoreCase(String str1, String str2) {
        return str1.toUpperCase().startsWith(str2.toUpperCase());
    }

}
