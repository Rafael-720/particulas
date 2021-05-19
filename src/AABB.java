public class AABB {

	////////////////////////////////////////////////////////////////////////////////
	// O AABB é composto por 2 pontos que determinam seu canto superior esquerdo  //
	// (x1, y1) e canto inferior direito (x2, y2). A posição padrão será 0,0 e o  // 
	// tamanho padrão usando este modelo será width = 50, height = 50,            //
	// "nas proximidades" marcará este AABB como estando próximo de outro AABB.   //
	////////////////////////////////////////////////////////////////////////////////
	float x1, y1;
	float x2, y2;
	float wdth, hght;
	float dx, dy;
	AABB nearby;
	
	/*******************************************************************************
	 * Construtor para a classe AABB que definirá as coordenadas do canto superior  
	 * esquerdo para 0,0 e as coordenadas do canto inferior direito para 50,50. Isso
	 * significa que a largura e a altura serão definidas para 50,50 respectivamente.
	 ******************************************************************************/
	public AABB(){
		x1 = 0;
		y1 = 0;
		x2 = 50;
		y2 = 50;
		dx = 0;
		dy = 0;
		wdth = x2;
		hght = y2;
		nearby = null;
	}
	
	/*******************************************************************************
	 * Construtor da classe AABB com parâmetros para definir localização e tamanho.
	 * 
	 * @param _x1 A coordenada 'x' superior esquerda deste AABB.
	 * @param _y1 A coordenada 'y' superior esquerda deste AABB.
	 * @param _x2 A coordenada 'x' inferior direita deste AABB.
	 * @param _y2 A coordenada 'y' inferior direita deste AABB.
	 ******************************************************************************/
	public AABB(int _x1, int _y1, int _x2, int _y2){
		x1 = _x1;
		y1 = _y1;
		x2 = _x2;
		y2 = _y2;
		dx = 0;
		dy = 0;
		wdth = x2-x1;
		hght = y2-y1;
		nearby = null;
	}
	
	/*******************************************************************************
	 * Determina se este AABB se cruza ou não com outro AABB.
	 *
	 * @param aabb O AABB contra o qual verificar a interseção com este AABB.
	 * @return true se este AABB cruzar com o aabb fornecido, caso contrário, retornar
	 * false.
	 ******************************************************************************/
	public boolean collides_With(AABB aabb){
		if(x1 > aabb.x2 || aabb.x1 > x2)
			return(false);
	
		if(y2 < aabb.y1 || aabb.y2 < y1)
			return(false);

		return(true);
	}
	
	/*******************************************************************************
	 * Move este AABB por delta_x, delta_y.
	 *
	 * @param delta_x O valor delta para o qual incrementar a posição "x"
	 * deste AABB.
	 * @param delta_y O valor delta para o qual incrementar a posição "y"
	 * deste AABB.
	 ******************************************************************************/
	public void relocate(float delta_x, float delta_y) {
		x1 += delta_x;
		y1 += delta_y;
		x2 += delta_x;
		y2 += delta_y;
	}
	
	/*******************************************************************************
	 * Alterna se este AABB está próximo a outros AABBs.
	 *
	 * @param nrby O AABB deve ser definido como o objeto próximo deste AABB.
	 ******************************************************************************/
	public void set_Nearby(AABB nrby){
		nearby = nrby;
	}
	
	/*******************************************************************************
	 * Define o tamanho alterando x2, y2 para a distância w, h de x1, y1.
	 * 
	 * @param w O valor de largura desejado para este AABB.
	 * @param h O valor de altura desejado para este AABB.
	 ******************************************************************************/
	public void set_Size(float w, float h){
		x2 = x1 + w;
		y2 = y1 + h;
		wdth = x2 - x1;
		hght = y2 - y1;
	}
	
	/*******************************************************************************
	 * Muda a direção e velocidade deste AABB.
	 *
	 * @param delta_x O valor x para velocidade e direção do movimento.
	 * @param delta_y O valor y para velocidade e direção do movimento.
	 ******************************************************************************/
	public void set_Velocity(float delta_x, float delta_y){
		dx = delta_x;
		dy = delta_y;
	}
}
