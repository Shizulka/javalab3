import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;

public class Graph extends JPanel {
    private final int[][] matrix;
    private final Point[] positions;
    private final boolean directed;

    public Graph(int[][] matrix, Point[] positions, boolean directed) {
        this.matrix = matrix;
        this.positions = positions;
        this.directed = directed;
        setPreferredSize(new Dimension(800, 800));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGraph((Graphics2D) g);
    }

    private void drawGraph(Graphics2D g2) {
        g2.setStroke(new BasicStroke(2));
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] == 1) {
                    Point from = positions[i];
                    Point to = positions[j];
                    if (i == j) {
                        drawSelfLoop(g2, from);
                    } else if (!directed && j < i) {
                    } else {
                        drawEdge(g2, from, to, directed);
                    }
                }
            }
        }


        for (int i = 0; i < positions.length; i++) {
            Point p = positions[i];
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillOval(p.x - 15, p.y - 15, 30, 30);
            g2.setColor(Color.BLACK);
            g2.drawOval(p.x - 15, p.y - 15, 30, 30);
            g2.drawString("V" + i, p.x - 10, p.y - 20);
        }
    }

    private void drawEdge(Graphics2D g2, Point from, Point to, boolean arrow) {
        g2.setColor(Color.BLUE);

        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        double unitX = dx / dist;
        double unitY = dy / dist;

        int r = 15;
        int startX = (int) (from.x + unitX * r);
        int startY = (int) (from.y + unitY * r);
        int endX = (int) (to.x - unitX * r);
        int endY = (int) (to.y - unitY * r);

        g2.drawLine(startX, startY, endX, endY);

        if (arrow) {
            drawArrowHead(g2, new Point(startX, startY), new Point(endX, endY));
        }
    }


    private void drawArrowHead(Graphics2D g2, Point from, Point to) {
        double phi = Math.toRadians(30);
        int barb = 12;

        double dy = to.y - from.y;
        double dx = to.x - from.x;
        double theta = Math.atan2(dy, dx);

        for (int i = 0; i < 2; i++) {
            double rho = theta + (i == 0 ? phi : -phi);
            int x = (int) (to.x - barb * Math.cos(rho));
            int y = (int) (to.y - barb * Math.sin(rho));
            g2.drawLine(to.x, to.y, x, y);
        }
    }


    private void drawSelfLoop(Graphics2D g2, Point p) {
        g2.setColor(Color.MAGENTA);
        Arc2D loop = new Arc2D.Double(p.x - 15, p.y - 30, 30, 30, 0, 270, Arc2D.OPEN);
        g2.draw(loop);
    }



    public static int[][] generateUndirectedMatrix(int n, double k) {
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (i == j) {
                    matrix[i][j] = 0;
                } else {
                    int value = (Math.random() * 3 * k < 1.0) ? 0 : 1;
                    matrix[i][j] = value;
                    matrix[j][i] = value;
                }
            }
        }
        return matrix;
    }

    public static int[][] generateDirectedMatrix(int n, double k) {
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = (i == j) ? 0 : ((Math.random() * 3 * k < 1.0) ? 0 : 1);
            }
        }
        return matrix;
    }

    public static Point[] generateGridPositions(int width, int height, int count) {
        Point[] points = new Point[count];

        int margin = 80;
        int left = margin, right = width - margin;
        int top = margin, bottom = height - margin;


        points[0] = new Point(left, top);
        points[1] = new Point((left + right) / 2, top);
        points[2] = new Point(right, top);
        points[3] = new Point(right, (top + bottom) / 3);
        points[4] = new Point(right, (top + bottom) * 2 / 3);
        points[5] = new Point(right, bottom);
        points[6] = new Point((left + right) / 2, bottom);
        points[7] = new Point(left, bottom);
        points[8] = new Point(left, (top + bottom) * 2 / 3);
        points[9] = new Point(left, (top + bottom) / 3);
        points[10] = new Point((left + right) / 2, (top + bottom) / 2);

        return points;

    }

    private static String matrixToString(int[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : matrix) {
            for (int val : row) {
                sb.append(val).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        int n = 11;
        double k = 0.685;

        String[] options = {"Undirected", "Directed"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Виберіть тип графа:",
                "Graph Type",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        boolean directed = (choice == 1);
        int[][] matrix = directed
                ? generateDirectedMatrix(n, k)
                : generateUndirectedMatrix(n, k);

        Point[] positions = generateGridPositions(600, 500, n);

        JOptionPane.showMessageDialog(null,
                matrixToString(matrix),
                "Матриця суміжності",
                JOptionPane.INFORMATION_MESSAGE);

        JFrame frame = new JFrame(directed ? "Directed Graph" : "Undirected Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Graph(matrix, positions, directed));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}



