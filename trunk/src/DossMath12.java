import java.util.Iterator;

public class DossMath12 {
	private static final double BASE = 3800;
	private static final double PRIME = 50;
	private static final double BONUS = 250;
	private static final double DELTA = 0.75;
	private static final Relation COL = Io.chargerRelation("Col.rel");
	private static final Relation FIN = Io.chargerRelation("Fin.rel");
	private static final Relation CPT = Io.chargerRelation("Cpt.rel");
	private static final Relation SUP = Io.chargerRelation("Sup.rel");
	private static final Relation CCN = Io.chargerRelation("Ccn.rel");
	private static final Relation SEX =  Io.chargerRelation("Sex.rel");

	private static final String[] tPers = Io.chargerDta("Pers.dta");
	private static final int nbPers = Integer.parseInt(tPers[0]); // nombre de membres du personnel
	
	private static final String[] tProj = Io.chargerDta("Proj.dta");
	private static final int nbProj = Integer.parseInt(tProj[0]); // nombre d'options
	
	private static final String[] tQual = Io.chargerDta("Qual.dta");
	private static final int nbQual = Integer.parseInt(tQual[0]);// nombre de cours
	
	private static Relation LAP;
	private static Relation affImpossible;
	
	private static Relation PSF;
	private static Ordre hierarchie;
	
	public static void main(String[] args) throws MathException {
			question1();
			question2();
			question3();
			question4();
			question5();
	} // main
	
	public static void question1(){
		System.out.println("Question 1");
		System.out.println("******************************************************************************************");
		System.out.println("Réponse 1.1");
		lister(COL.depart().moins(COL.domaine()), "PERSONNELS");
		System.out.println("Réponse 1.2");
		lister(CPT.arrivee().moins(CPT.image()), "QUALIFICATIONS");
		System.out.println("Réponse 1.3");
		if(SUP.acyclique()) System.out.println("La relation SUP est acyclique");
		else System.out.println("La relation SUP n'est pas acyclique");
		System.out.println("Réponse 1.4");
		LAP = (CCN.reciproque()).apres(CPT); // Pas sur
		System.out.println("Réponse 1.5");
		lister(LAP.imageReciproque(new Elt(numéro("Antoinet", "PROJETS"))), "PERSONNELS");
		//Question 1.6, je m'en charge mtn ! (24/03/2012)
		System.out.println("Réponse 1.6");
		int max = 0;
		Iterator<Elt> itMax = LAP.domaine().iterator();
		while(itMax.hasNext()){
			max = Math.max(max, LAP.degreDeSortie(itMax.next()));
		}
		itMax=LAP.domaine().iterator();
		Ensemble ens = new Ensemble();
		while(itMax.hasNext()){
			Elt elem = itMax.next();
			if(LAP.degreDeSortie(elem) == max) ens.ajouter(elem);
		}
		System.out.println("Membres du personnel ayant le maximum d'affectations possibles");
		lister(ens, "PERSONNELS");
		
		System.out.println("Réponse 1.7");
		affImpossible = COL.clone();
		affImpossible.enlever(LAP);
		if(affImpossible.cardinal()!=0){
			Iterator<Couple> it = affImpossible.iterator();
			System.out.println("Liste des membres du personnel collaborant à un projet ne figurant pas dans la liste de leurs affectations possibles : ");
			while(it.hasNext()){
				Couple c = it.next();
				lister(affImpossible.imageReciproque(c.getx()), "PERSONNELS");
				System.out.println("Collaborant aux projets : ");
				lister(affImpossible.imageDirecte(c.gety()),"PROJETS");
			}
		}else{
			System.out.println("Aucun membre du personnel ne collabore à un projet ne faisant pas partie de ses affectations possibles.");
		}
	}
	
	public static void question2(){
		System.out.println("\nQuestion 2");
		System.out.println("******************************************************************************************");
		System.out.println("Réponse 2.1");
		
		Relation finLu = new Relation(FIN.depart(), FIN.arrivee());
		Iterator<Couple> itLu = FIN.iterator();
		while(itLu.hasNext()){
			Couple c = itLu.next();
			if(LAP.contient(c)){
				finLu.ajouter(c);
			}
		}
		
		Ensemble cat1 = FIN.depart().clone();
		cat1.enlever(FIN.domaine());
		
		Ensemble cat2 = FIN.domaine().clone();
		Iterator<Couple> it = FIN.iterator();
		while(it.hasNext()){
			Couple c = it.next();
			if(LAP.contient(c)){
				cat2.enlever(c.getx());
			}
		}
		
		Ensemble cat3 = new Ensemble();
		it = FIN.iterator();
		while(it.hasNext() ){
			Couple c = it.next();
			if(LAP.contient(c) && FIN.degreDeSortie(c.getx())>finLu.degreDeSortie(c.getx())){
				cat3.ajouter(c.getx());
			}
		}
		
		Ensemble cat4 = new Ensemble();
		it = FIN.iterator();
		while(it.hasNext() ){
			Couple c = it.next();
			if(LAP.contient(c) && FIN.degreDeSortie(c.getx())==finLu.degreDeSortie(c.getx())){
				cat4.ajouter(c.getx());
			}
		}
		
		
		System.out.println("Catégorie 1 : ");
		lister(cat1, "PERSONNELS");
		System.out.println("Catégorie 2 : ");
		lister(cat2, "PERSONNELS");
		System.out.println("Catégorie 3 : ");
		lister(cat3, "PERSONNELS");
		System.out.println("Catégorie 4 : ");
		lister(cat4, "PERSONNELS");
		
		System.out.println("Réponse 2.2");
		
		double compteur1f =0;
		double compteur2f =0;
		double compteur3f =0;
		double compteur4f =0;
		
		Elt femme = new Elt(2);
		Iterator<Couple> itSex = SEX.iterator();
		while(itSex.hasNext()){
			Couple c = itSex.next();
			if(cat1.contient(c.getx())){
				if(c.gety().estEgalA(femme)){
					compteur1f++;
				}
			}else{
				if(cat2.contient(c.getx())){
					if(c.gety().estEgalA(femme)){
						compteur2f++;
					}
				}else{
					if(cat3.contient(c.getx())){
						if(c.gety().estEgalA(femme)){
							compteur3f++;
						}
					}else{
						if(cat4.contient(c.getx())){
							if(c.gety().estEgalA(femme)){
								compteur4f++;
							}
						}
					}
				}
			}
		}
		
		compteur1f = compteur1f/cat1.cardinal();
		compteur2f = compteur2f/cat2.cardinal();
		compteur3f = compteur3f/cat3.cardinal();
		compteur4f = compteur4f/cat4.cardinal();
		
		System.out.println("La/les catégorie(s) comportant la plus grande proportion de femmes est/sont : ");
		double max = Math.max(Math.max(Math.max(compteur1f, compteur2f), compteur3f), compteur4f);
		if(compteur1f==max){
			System.out.println("Catégorie 1 : Aucun financement ");
		}
		if(compteur2f==max){
			System.out.println("Catégorie 2 : Aucun soutien financier lucide");
		}
		if(compteur3f==max){
			System.out.println("Catégorie 3 : Des soutiens financiers lucides et d'autres soutiens financiers ");
		}
		if(compteur4f==max){
			System.out.println("Catégorie 4 : Seulement des soutiens financiers lucides");
		}
		
	}

	public static void question3(){
		System.out.println("\nQuestion 3");
		System.out.println("******************************************************************************************");
		// TO DO
		System.out.println("Question 3.1");
		peutSuivreLaFormationEnInit();
		System.out.println("peut suivre la formation en (Relation PSF) Initialisé");
		System.out.println("Question 3.2");
		lister(PSF.imageDirecte(numéro("KOEKELBERG Basile", "PERSONNELS")), "QUALIFICATIONS");
		System.out.println("Question 3.3");
		Ensemble quest33 = new Ensemble();
		Iterator<Elt> it = PSF.depart().iterator();
		while(it.hasNext()){
			Elt el = it.next();
			if(PSF.degreDeSortie(el) == 0){
				quest33.ajouter(el);
			}
		}
		lister(quest33,"PERSONNELS");
	}
	
	private static void peutSuivreLaFormationEnInit(){
		PSF = new Relation(COL.depart().clone(), CCN.arrivee().clone());
		Iterator<Couple> itCol = COL.iterator();
		//Iterator<Couple> it = CCN.iterator();
		

		while(itCol.hasNext()){
			Couple couple = itCol.next();
			Elt pers = couple.getx();
			Elt projet = couple.gety(); // y désigne le domaine de qualification
			// Regarder les qualifications demandés pour le projet
			Ensemble qualifProjet = CCN.imageDirecte(projet);
			// Regarder les qualifications du personnel
			Ensemble qualifPers = CPT.imageDirecte(pers);
			Iterator<Elt> itQualifProjet = qualifProjet.iterator();
			while(itQualifProjet.hasNext()){
				Elt x = itQualifProjet.next();
				if(!qualifPers.contient(x)){
					PSF.ajouter(pers, x);
				}
				
			}
			
		}

	}

	public static void question4(){
		System.out.println("\nQuestion 4");
		System.out.println("******************************************************************************************");
		System.out.println("Réponse 4.1 : ");
		
		Relation aPourChef = new Relation(COL.arrivee(), COL.depart());
		hierarchie = new Ordre(SUP.reciproque());
		Iterator<Elt> it = COL.arrivee().iterator();
         while(it.hasNext()){
             Elt elem = it.next();
             Elt max = hierarchie.maximum(COL.imageReciproque(elem));
             if( max != null){
                 aPourChef.ajouter(elem,max);
             }
         }
		
		
		System.out.println("Réponse 4.2 : ");
		lister(COL.arrivee().moins(aPourChef.domaine()),"PROJETS");
		System.out.println("Réponse 4.3 : ");
		Iterator<Elt> itMP = aPourChef.image().iterator();
		boolean chefPlPro = true;
		while(itMP.hasNext()){
			if(aPourChef.degreDEntree(itMP.next())>1){
				System.out.println("Il existe un membre du personnel chef de plusieurs projets");
				chefPlPro = true;
				break;
			}else{
				chefPlPro = false;
			}
			
		}
		if(!chefPlPro) System.out.println("Il n'y a pas de membre du personnel chef de plusieurs projets");
	}

	public static void question5(){
		System.out.println("\nQuestion 5");
		System.out.println("******************************************************************************************");
		System.out.println("Réponse 5.1 : ");
		Ordre or = new Ordre(SUP.reciproque());
		lister(or.maximaux(SUP.depart()),"PERSONNELS");
		System.out.println("Réponse 5.2 : ");
		// TO DO
	}

	public static void question6(){
		System.out.println("Question 6");
		System.out.println("******************************************************************************************");
		System.out.println("Réponse 6.1 : ");
		
		System.out.println("Réponse 6.2 : ");
		System.out.println("Cet ordre n'est pas total. Si deux qualifications ont les mêmes proportions, aucune ne sera moins prioritaires que l'une que l'autre. Elles n'auraient donc pas de lien entre elles.");
		System.out.println("Réponse 6.3 : ");
		
	}

	public static void question7(){
		System.out.println("Question 7");
		System.out.println("******************************************************************************************");
		// TO DO
	}
	


	// affiche à l'écran les éléments de e, interprétés selon contexte
	private static void lister(EnsembleInterface e, String contexte)
			throws MathException {
		String[] t;
		String ct;
		if (contexte.toUpperCase().equals("PERSONNELS")) {
			t = tPers;
			ct = "de membres du personnel";
		} else if (contexte.toUpperCase().equals("PROJETS")) {
			t = tProj;
			ct = "de projets";
		} else if (contexte.toUpperCase().equals("QUALIFICATIONS")) {
			t = tQual;
			ct = "de domaines de qualification";
		} else
			throw new MathException("contexte incorrect");
		if (e.estVide())
			System.out.println("Ensemble vide " + ct);
		else {
			Iterator<Elt> eit = e.iterator();
			while (eit.hasNext()) {
				int x = eit.next().val();
				System.out.println("        " + x + "\t" + t[x]);
			}
		}
	} // lister

	// renvoie le Elt correpondant à nom, d'après contexte; null si incorrect
	private static Elt numéro(String nom, String contexte) throws MathException {
		String[] t;
		int stop;
		if (contexte.toUpperCase().equals("PERSONNELS")) {
			t = tPers;
			stop = nbPers;
		} else if (contexte.toUpperCase().equals("PROJETS")) {
			t = tProj;
			stop = nbProj;
		} else if (contexte.toUpperCase().equals("QUALIFICATIONS")) {
			t = tQual;
			stop = nbQual;
		} else
			throw new MathException("contexte incorrect");
		for (int i = 1; i <= stop; i++)
			if (t[i].toUpperCase().equals(nom.toUpperCase()))
				return new Elt(i);
		return null;
	} // numéro

} // class
