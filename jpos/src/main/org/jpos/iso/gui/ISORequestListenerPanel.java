/**
 * ISORequestListenerPanel
 * Allows visualization of unhandled ISOMUX messages
 * @author apr@cs.com.uy
 * @see org.jpos.iso.ISOMsg
 * @see org.jpos.iso.ISOMUX
 * @see ISOMsgPanel
 */

/*
 * $Log$
 * Revision 1.5  2000/03/01 14:44:45  apr
 * Changed package name to org.jpos
 *
 * Revision 1.4  1999/09/06 17:20:22  apr
 * Added Logger SubSystem
 *
 * Revision 1.3  1999/08/06 11:40:12  apr
 * expand -4
 *
 * Revision 1.2  1999/07/29 15:55:12  apr
 * Added LOG_CAPACITY checks
 *
 * Revision 1.1  1999/05/18 12:02:59  apr
 * Added GUI package
 *
 */

package org.jpos.iso.gui;

import java.util.Observer;
import java.util.Observable;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.jpos.iso.*;

public class ISORequestListenerPanel extends JPanel implements Observer {
    /**
     * @serial
     */
    DefaultListModel log;
    /**
     * @serial
     */
    String symbolicName;
    public static final int LOG_CAPACITY = 250;

    public ISORequestListenerPanel (
        ISORequestListener requestListener,
        String symbolicName)
    {
        super();
        this.symbolicName = symbolicName;
        setLayout(new FlowLayout());
        setBorder(BorderFactory.createRaisedBevelBorder());
        log = new DefaultListModel();
        add(createPanel());
        requestListener.addObserver(this);
    }
    public final String getSymbolicName() {
        return symbolicName;
    }
    public final ListModel getLog() {
        return log;
    }

    public void update(Observable o, Object arg) {
        ISORequestListener l = (ISORequestListener) o;
        if (arg != null && arg instanceof ISOMsg) {
            ISOMsg m = (ISOMsg) arg;
            try {
                String mti = (String) m.getValue(0);
                int imti   = Integer.parseInt(mti);
                log.addElement(m);
                if (log.getSize() > LOG_CAPACITY) 
                    log.remove(0);
            } catch (ISOException e) { }
        }
    }

    private JPanel createPanel() {
        JPanel A = new JPanel() {
            public Insets getInsets() {
                return new Insets(10,10,10,10);
            }
        };

        A.setLayout(new BorderLayout());

        JLabel l = new JLabel(symbolicName);
        A.add(l, BorderLayout.NORTH);

        final JList logList = new JList(log);
        logList.setPrototypeCellValue("9999 99999999 999999");

        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                ISOMsg m = (ISOMsg) logList.getSelectedValue();
                if (m != null) {
                    JFrame f = new JFrame(m.toString());
                    ISOMsgPanel p = new ISOMsgPanel(m);
                    f.getContentPane().add(p);
                    f.pack();
                    f.show();
                }
            }
        };
        logList.addMouseListener(mouseListener);

        JScrollPane scrollPane = new JScrollPane(logList);
        scrollPane.setPreferredSize(new Dimension(170,200));
        A.add(scrollPane, BorderLayout.SOUTH);
        return A;
    }
}
