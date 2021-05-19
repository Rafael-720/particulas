
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Random;
import java.util.Scanner;

public class Tester extends JPanel {

	// window components
	JFrame frame;
	JPanel menu_Bar;
	JPanel side_Bar;
	JButton license_Bttn;
	JButton about_Bttn;
	JButton plus_TD_Bttn;
	
	JButton plus_50_Bttn;
	
	JButton minus_AABB_Bttn;
	JButton remove_AABBs_Bttn;
	JButton toggle_Square_Bttn;
	JButton grow_AABBs_Bttn;
	JButton shrink_AABBs_Bttn;

	int hght;
	int wdth;
	int x;
	int y;
	int menu_Bar_Height;
	int number_Of_AABBs;
	int max_Tree_Depth;
	long screen_Refresh;
	static boolean PAUSED;
	boolean square_Quadtree;
	Image img_Buffer;
	Graphics g2D;
	Color quadtree_Color;
	Color common_Quadnode_Color;
	Color aabb_Color;
	Color aabb_Nearby_Color;

	AABB[] aabbs;
	Quadtree quadtree;

	/*******************************************************************************
	 * Configura a largura e altura do canvas, bem como a localiza��o de seu canto
	 * superior esquerdo para que o canvas seja centralizado na tela.
	 ******************************************************************************/
	public Tester() {
		
		long tempoInicial = System.currentTimeMillis();
		hght = (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.8);
		wdth = (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.7);
		x = ((int) (Toolkit.getDefaultToolkit().getScreenSize().width - wdth) / 2) - 70;
		y = ((int) (Toolkit.getDefaultToolkit().getScreenSize().height - hght) / 2);
		number_Of_AABBs = 300; // Quantidade de part�culas
		max_Tree_Depth = 6;
		screen_Refresh = 1000 / 60;
		PAUSED = false;
		square_Quadtree = false;
		quadtree_Color = new Color(0.5f, 0.5f, 0.5f);
		common_Quadnode_Color = new Color(0.17f, 0.17f, 0.17f);
		aabb_Color = new Color(new Random().nextInt(50), 110 + new Random().nextInt(60), 110 + new Random().nextInt(60), 220);
		aabb_Nearby_Color = new Color(255, 0, 0, 220);

		// aqui setamos o array de AABBs com valores aleatórios para cada AABB.
		aabbs = new AABB[number_Of_AABBs];
		for (int i = 0; i < aabbs.length; i++) {
			aabbs[i] = new AABB();
			aabbs[i].set_Size(20 + new Random().nextInt(hght / 20), 20 + new Random().nextInt(hght / 20));
			aabbs[i].set_Velocity(3 - new Random().nextFloat() * 6, 3 - new Random().nextFloat() * 6);
			aabbs[i].relocate(1 + new Random().nextInt(wdth - (int) aabbs[i].x2 - (int) aabbs[i].x1 - 2), 1 + new Random().nextInt(hght - (int) aabbs[i].y2 - (int) aabbs[i].y1 - 2));
		}

		/////////////////////////////////////////////////////////////////////////////
		// variaveis padrões para a simulão que está acima                        //
		/////////////////////////////////////////////////////////////////////////////
		// setando a cor e o tamanho do JPanel pra desenhar
		setLayout(null);
		setBackground(Color.DARK_GRAY);
		setPreferredSize(new Dimension(wdth, hght));

		// barra lateral caso queira adicionar botões para testar o quadtree
		side_Bar = new JPanel();
		side_Bar.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		side_Bar.setLayout(null);
		side_Bar.setPreferredSize(new Dimension(160, 100));

		// cria o frame e o adiciona ao JPanel.
		
		frame = new JFrame(get_Graphics_Configuration());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocation(x, y - 60);
		frame.setLayout(new BorderLayout());
		frame.add(BorderLayout.WEST, this);
		frame.add(BorderLayout.EAST, side_Bar);
		
		frame.pack();

		

		// Inicializa o Quadtree pelo JPanel, calculando seu tamanho
		quadtree = new Quadtree(aabbs, wdth, hght, max_Tree_Depth, square_Quadtree);

		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
		}

		
		frame.setVisible(true);
		long tempoFinal = System.currentTimeMillis() - tempoInicial;
		System.out.println("O processo levou " + tempoFinal + " milisegundos");
		update_Scene();
	}

	
	
	/*******************************************************************************
	 * Retorna configurações graficas deste monitor para ser usado com o frame
	 ******************************************************************************/
	private GraphicsConfiguration get_Graphics_Configuration(){
		Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
       if (focusOwner != null) {
           Window w = SwingUtilities.getWindowAncestor(focusOwner);
           if (w != null) {
               return w.getGraphicsConfiguration();
           } else {
               for(Frame f : Frame.getFrames()) {
                   if("NbMainWindow".equals(f.getName())) {
                       return f.getGraphicsConfiguration();
                   }
               }
           }
       }

       return(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
	}

	
	/*******************************************************************************
	 * Pinta o Quadtree e todos os objetos baseado na atual localização dos objetos em tela junto com a mesagem que esteja presente 
	 *
	 * @param g 
	 ******************************************************************************/
	public void paint(Graphics g) {
		if (img_Buffer == null)
			img_Buffer = createImage(wdth, hght);
		else{
			g2D = img_Buffer.getGraphics();
			super.paint(g2D);
			paint_Quadtree(g2D);
			paint_AABBs(g2D);
			paint_Messages(g2D);
			//g2D.dispose();
			
			// copy image buffer to screen
			g.drawImage(img_Buffer, 0, 0, null);
		}
	}

	/*******************************************************************************
	 * Pinta o Quadtree e todos os objetos na tela baseado na localização atual do obejto.
	 *
	 * @param g 
	 * ****************************************************************************/
	private void paint_AABBs(Graphics g) {
		for (AABB aabb : aabbs) {
			if (aabb.nearby == null) {
				g.setColor(aabb_Color);
			} else {
				g.setColor(aabb_Nearby_Color);
			}
			g.fillRect((int) aabb.x1, (int) aabb.y1, (int) aabb.wdth, (int) aabb.hght);
		}
	}

	/*******************************************************************************
	 * Pinta uma mensagem no meio da tela quando pressiona o botão espaço e pausa a aplicação, mas retirei por fugir do escopo.
	 *
	 * @param g Objeto grafico para pintar.
	 ******************************************************************************/
	private void paint_Messages(Graphics g) {
		if (PAUSED) {
			g.setColor(new Color(0, 0, 0, 127));
			g.fillRect(wdth / 2 - 206, hght / 2 - 24, 410, 60);
			g.setColor(Color.WHITE);
			g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
			g.drawString("- Paused -", wdth / 2 - 54, hght / 2);
			g.drawString("Press <Space> to continue simulation", wdth / 2 - 200, hght / 2 + 20);
		}
	}

	/*******************************************************************************
	 * Pinta cada Quadnode que contem o ultimo objeto.
	 *
	 * @param g Objeto grafico para pintar.
	 * @param node Nó para pintar.
	 ******************************************************************************/
	private void paint_Quadnode(Graphics g, Quadtree.Quadnode node) {
		if (node.tree_Depth<max_Tree_Depth) {
			if (node.aabbs.size() > 1) {
				if(node.tree_Depth == max_Tree_Depth-1){
					g.setColor(common_Quadnode_Color);
					g.fillRect(node.x1, node.y1, node.x2 - node.x1, node.y2 - node.y1);
				}
				g.setColor(quadtree_Color);
				g.drawRect(node.x1, node.y1, node.x2 - node.x1, node.y2 - node.y1);
			}

			paint_Quadnode(g, node.nw);
			paint_Quadnode(g, node.ne);
			paint_Quadnode(g, node.sw);
			paint_Quadnode(g, node.se);
		}
	}

	/*******************************************************************************
	 * Pinta o Quadtree dizendo a cada nó dentro de sua hierarquia para pintar
	 * próprio para o objeto gráfico fornecido.
	 *
	 * @param g The graphics object to paint to.
	 ******************************************************************************/
	private void paint_Quadtree(Graphics g) {
		paint_Quadnode(g, quadtree.root);
	}


	/*******************************************************************************
	 *  
	 * Alterna entre Quadtree quadrado ou retangular. Quadtree retangular
	 * vai caber no componente da janela em que se encontra, enquanto um Quadtree quadrado terá
	 * uma parte da parte inferior se estendendo além da parte inferior da janela
	 * componente. O Quadtree deve ser reconstruído após essa alteração, portanto
	 * "quadtree.reshape (wdth, hght, square_Quadtree)" é chamado no final deste
	 * método.
	 * ****************************************************************************/
	public void toggle_Square_Quadtree() {
		square_Quadtree = !square_Quadtree;
		if (square_Quadtree) {
			toggle_Square_Bttn.setText("<html>Rectangulate<br/>Quadtree</html>");
		} else {
			toggle_Square_Bttn.setText("<html>Square<br/>Quadtree</html>");
		}
		quadtree.reshape(wdth, hght, square_Quadtree);
	}

	

	/*******************************************************************************
	 * Repete continuamente a renderização da tela até sair.  Este método irá
	 * atualizar as localizações do quadrado, atualizar o Quadtree e atualizar a tela
	 * chamando "repaint ()".  Tudo isso é feito cerca de 60 vezes por
	 * segundo.
	 ******************************************************************************/
	public void update_Scene() {
		while (true) {
			if(!PAUSED){
				long time_Start = System.currentTimeMillis();
				update_AABB_locations();
				quadtree.update();
				long time_Elapsed = System.currentTimeMillis() - time_Start;

				try {
					if (time_Elapsed < screen_Refresh) {
						Thread.sleep(screen_Refresh - time_Elapsed);
					}
				} catch (InterruptedException e) {}
			}
			repaint();
		}
	}

	/*******************************************************************************
	 * 
	 * Faz a iteração pela matriz AABBs e diz a todos para atualizarem seus
	 * locais. Este método moverá os AABBs dentro dos limites definidos por
	 * wdth e hght. Se o AABB estiver se movendo para fora dos limites, reverte dx ou dy como
	 * necessário para que o AABB se afaste dos limites.
	 ******************************************************************************/
	private void update_AABB_locations() {
		for (AABB aabb : aabbs) {
			if (aabb.x2 + aabb.dx >= wdth || aabb.x1 + aabb.dx <= 0) {
				aabb.dx = -aabb.dx;
			}
			if (aabb.y2 + aabb.dy >= hght || aabb.y1 + aabb.dy <= 0) {
				aabb.dy = -aabb.dy;
			}
			aabb.relocate(aabb.dx, aabb.dy);
		}
	}

	/*******************************************************************************
	 * metodo main.
	 ******************************************************************************/
	public static void main(String[] args) {
		new Tester();
	}
}
