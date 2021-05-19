
import java.util.*;
public class Quadtree {

	Quadnode root;
	int max_Tree_Depth;
	AABB[] all_AABBs;

	/*******************************************************************************
	 * Classe Quadnode; Ela cont�m duas coordenadas que s�o usadas para determinar
	 * seu canto superior esquerdo (x1, y1) e seu canto inferior direito (x2, y2)
	 * destinados a c�lculo eficiente. A combina��o dessas coordenadas ser� 
	 * usada para medir a largura, altura e pontos centrais dos quadrantes.
	 *******************************************************************************/
	public class Quadnode {

		int x1, y1, x2, y2, cntr_x, cntr_y;
		int tree_Depth;
		Quadnode nw, ne, sw, se;
		LinkedList<AABB> aabbs;

		/****************************************************************************
		 * Construtor para criar um n� Quadtree padr�o. Como um AABB, ele � feito
		 * acima de dois pontos de coordenadas para determinar seu canto superior
		 * esquerdo e inferior direito O ponto central (cntr_x, cntr_y) tamb�m �
		 * calculado quando este construtor � chamado. Quadnodes cont�m uma lista
		 * (inicialmente vazia) de objetos que cruzam seus limites. 
		 *
		 * @param _x1 A posi��o x superior esquerda deste quadnode.
		 * @param _y1 A posi��o y superior esquerda deste quadnode.
		 * @param _x2 A posi��o x inferior direita deste quadnode.
		 * @param _y2 A posi��o y inferior direita deste quadnode.
		 * @param _tree_Depth A profundidade da �rvore desejada para este quadnode.
		 ***************************************************************************/
		public Quadnode(int _x1, int _y1, int _x2, int _y2, int _tree_Depth) {
			x1 = _x1;
			y1 = _y1;
			x2 = _x2;
			y2 = _y2;
			cntr_x = (x1 + x2) / 2;
			cntr_y = (y1 + y2) / 2;
			tree_Depth = _tree_Depth;
			aabbs = new LinkedList<>();
		}
	}
	
	/*******************************************************************************
	 * Construtor do Quadtree. Constr�i o Quadtree de cima para baixo chamando
	 * "build_Quadtree" para iniciar o processo. Se square for "true", antes de 
	 * construir os Quadnodes, dimensiona o n� raiz para que seja quadrado (ambas
	 * largura e a altura ser�o a maior largura e altura da janela). Se a raiz do
	 * Quadnode n�o for quadrada, nenhum dos Quadnodes ser� quadrado e colis�es
	 * fantasma podem ocorrer com mais frequ�ncia no eixo onde os Quadnodes s�o mais largos
	 * (ou mais altos). Uma Quadtree em uma tela ou janela retangular significa que parte
	 * do Quadtree est� fora da tela, mas o desempenho deve permanecer o mesmo.
	 *
	 * @param aabb_Array A lista de AABBs a serem rastreados por este quadtree.            
	 * @param wdth A largura total deste quadtree.
	 * @param hght A altura total deste quadtree.
	 * @param mtd A profundidade m�xima da �rvore para este quadtree.
	 * @param square Se definido como "true", o quadtree ter� uma forma quadrada
	 * sen�o o quadtree ser� ret�ngular.
	 ******************************************************************************/
	public Quadtree(AABB[] aabb_Array, int wdth, int hght, int mtd, boolean square) {
		all_AABBs = aabb_Array;
		max_Tree_Depth = mtd;
		
		reshape(wdth, hght, square);
	}
	
	/*******************************************************************************
     * Cria e retorna recursivamente os Quadnodes at� que o Quadtree alcance a
	 * profundidade m�xima permitida da �rvore definida por "max_Tree_Depth". O
	 * Quadtree ser� completado com listas AABB vazias e pronto para ser usado
	 * para compartimentaliza��o recursiva r�pida de AABBs fornecidos ao Quadtree.
	 *
	 * @param _x1 A coordenada x superior esquerda deste quadtree.
	 * @param _y1 A coordenada y superior esquerda desta quadtree.
	 * @param _x2 A coordenada x inferior direita deste quadtree.
	 * @param _y2 A coordenada y inferior direita desta quadtree.
	 * @param td A profundidade da �rvore necess�ria para chamar o quadnode.
	 * @return Um novo Quadnode constru�do a partir dos par�metros fornecidos.
    ******************************************************************************/
	public Quadnode build_Quadtree(int _x1, int _y1, int _x2, int _y2, int td) {
		Quadnode node = null;
		if (td <= max_Tree_Depth) {
			node = new Quadnode(_x1, _y1, _x2, _y2, td);
			node.nw = build_Quadtree(_x1, _y1, node.cntr_x, node.cntr_y, td + 1);
			node.ne = build_Quadtree(node.cntr_x, _y1, _x2, node.cntr_y, td + 1);
			node.sw = build_Quadtree(_x1, node.cntr_y, node.cntr_x, _y2, td + 1);
			node.se = build_Quadtree(node.cntr_x, node.cntr_y, _x2, _y2, td + 1);
		}
		return (node);
	}
	
	/****************************************************************************
     * Repete a lista de AABB dos Quadnodes fornecidos e verifica onde os AABBs
	 * est�o nos quatro subquadnodes do Quadnode dado e os adicionam a esse
	 * subqu�druplo. Depois de passar pela lista AABB, chama recursivamente este
	 * m�todo contra os subquadnodes deste subquadnode que contem pelo menos 2
	 * itens em sua lista AABB e continua este processo at� que a profundidade
	 * m�xima da �rvore seja atingida ou chamadas recursivas n�o apresentem uma
	 * situa��o que requeira pesquisas adicionais. Se a a profundidade m�xima da
	 * �rvore � atingida, todos os AABBs dentro do quadnode que alcan�aram a
	 * profundidade m�xima da �rvore s�o considerados pr�ximos um do outro.
	 *
	 * @param node O n� de interesse a ser pesquisado.
	 ***************************************************************************/
	public void insert(Quadnode node) {
		if (node.tree_Depth < max_Tree_Depth) {
			if (node.aabbs.size() > 1) {
				for (AABB aabb : node.aabbs) {
					if (aabb.y1 < node.cntr_y) {
						if (aabb.x1 < node.cntr_x) {
							if (node.nw != null) {
								node.nw.aabbs.add(aabb);
							}
						}
						if (aabb.x2 > node.cntr_x) {
							if (node.ne != null) {
								node.ne.aabbs.add(aabb);
							}
						}
					}
					if (aabb.y2 > node.cntr_y) {
						if (aabb.x1 < node.cntr_x) {
							if (node.sw != null) {
								node.sw.aabbs.add(aabb);
							}
						}
						if (aabb.x2 > node.cntr_x) {
							if (node.se != null) {
								node.se.aabbs.add(aabb);
							}
						}
					}
				}
				insert(node.nw);
				insert(node.ne);
				insert(node.sw);
				insert(node.se);
			}
		} else {
			node.aabbs.forEach((aabb) -> {
				set_Nearby(node.aabbs, aabb);
			});
		}
	}

	/*******************************************************************************
	 * Repete recursivamente no Quadtree e apaga todas as listas AABB dentro dos
	 * Quadnodes.
	 *
	 * @param node O n� cuja lista AABB deve ser apagado.
	 ******************************************************************************/
	private void reset_Quadnodes(Quadnode node) {
		if (node != null) {
			if (!node.aabbs.isEmpty()) {
				node.aabbs.clear();
			}
			reset_Quadnodes(node.nw);
			reset_Quadnodes(node.ne);
			reset_Quadnodes(node.sw);
			reset_Quadnodes(node.se);
		}
	}
	
	/*******************************************************************************
	 * Define a forma deste quadtree como quadrado ou ret�ngulo.
	 *
	 * @param wdth A largura necess�ria deste quadtree. Se o valor de "square"
	 * for "true", wdth ser� ajustado para que seu valor seja o do maior de
	 * wdth e hght.
	 * @param hght A altura necess�ria deste quadtree. Se o valor de "square"
	 * for "true", hghtser� ajustado para que seu valor seja o do maior de
	 * wdth e hght.
	 * @param square Representa se esta quadtree ser� quadrada ou n�o.
	 * "false" indica que esta quadtree dever� ter forma retangular.
	 ******************************************************************************/
	public void reshape(int wdth, int hght, boolean square){
		if(square)
			wdth = hght = wdth > hght ? wdth - 1 : hght - 1;
		root = build_Quadtree(0, 0, wdth, hght, 0);
	}

	/*******************************************************************************
	 * Define a profundidade m�xima da �rvore para "mtd". O quadtree deve ser
	 * reconstru�do se a profundidade da �rvore for alterada.
	 * 
	 * @param mtd A profundidade m�xima da �rvore para este quadtree.
	 ******************************************************************************/
	public void set_Max_Tree_Depth(int mtd) {
		if (max_Tree_Depth != mtd) {
			max_Tree_Depth = (mtd < 1 || mtd > 10) ? max_Tree_Depth : mtd;
			root = build_Quadtree(root.x1, root.y1, root.x2, root.y2, 0);
			root.aabbs.addAll(Arrays.asList(all_AABBs));
		}
	}
	
	/*******************************************************************************
	 * Cada AABB na lista de aabb determinada definir� seu AABB "nearby" para o
	 * "target".
	 *
	 * @param target O AABB que ser� definido como o AABB "nearby" para todos os itens 
	 * na aabb_List.
	 * @param  aabb_List Lista os AABB cujo conte�do deve ter todos os
	 * AABBs "nearby" definidos como "target".
	 ******************************************************************************/
	private void set_Nearby(LinkedList<AABB> aabb_List, AABB target){						
		for(AABB aabb: aabb_List)
			aabb.set_Nearby(target);

		///////////////////////////////////////////////////////////////////////
		// At this point we can go to phase TWO of collision detection...    //
		///////////////////////////////////////////////////////////////////////
	}

	/*******************************************************************************
	 * Atualiza o Quadtree esvaziando as listas AABB dos Quadnodes folha e, em 
	 * seguida, insere os AABBs um ap�s o outro no Quadtree. Todas as listas de AABB
	 * "Nearby_AABBs" s�o apagadas at� que seja determinado novamente que h�
	 * AABBs pr�ximos conforme determinado pelo m�todo "insert".
	 *******************************************************************************/
	public void update() {
		reset_Quadnodes(root);
		root.aabbs.addAll(Arrays.asList(all_AABBs));
		for (AABB aabb : all_AABBs) {
			aabb.nearby = null;
		}
		insert(root);
	}
}
