import java.util.Iterator;


public class Ordre implements RelationInterface {
	
	private Relation or;
	
	@Override
	public boolean estVide() {
		return this.or.estVide();
	}

	@Override
	public boolean contient(Couple c) {
		return this.or.contient(c);
	}

	@Override
	public void ajouter(Couple c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enlever(Couple c) {
		// TODO Auto-generated method stub

	}

	@Override
	public EnsembleInterface depart() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnsembleInterface arrivee() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Couple> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

}
