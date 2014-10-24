import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

public class GenomeTable extends JPanel {

	public GenomeTable(Genome g) {
		super(new GridLayout(1, 0));

		String[] columnNames = { "#", "x1", "y1", "x2", "y2", "x3", "y3", "r",
				"g", "b", "a" };
		ArrayList<Object[]> objectArr = new ArrayList<Object[]>();

		for (int i = 0; i < 200; i++) {
			objectArr.add(new Object[] { i + 1, g.geneList.get(i).xpoints[0],
					g.geneList.get(i).ypoints[0], g.geneList.get(i).xpoints[1],
					g.geneList.get(i).ypoints[1], g.geneList.get(i).xpoints[2],
					g.geneList.get(i).ypoints[2], g.geneList.get(i).r,
					g.geneList.get(i).g, g.geneList.get(i).b,
					g.geneList.get(i).a });
		}

		Object[][] data = objectArr.toArray(new Object[0][]);

		JTable table = new JTable(data, columnNames);
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);
		JFrame frame = new JFrame("Genome Table\t" + g.IMG_WIDTH + "x"
				+ g.IMG_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(this);
		frame.pack();
		frame.setVisible(true);
	}

}