import java.util.Iterator;
/**
 * 
 * @author Erwan Corbisier
 * @author Mathieu Steenput
 * @title Dossier de mathématique 2012
 *
 */
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
	private static int[] tableau;
	
	
	/**
	 * LAP => Relation des listes des affections possible
	 */
	private static Relation LAP;
	
	/**
	 * affImpossible => Relation des listes des affectations impossible 
	 */
	private static Relation affImpossible;
	
	/**
	 * PSF => Relation peut suivre la formation en 
	 */
	private static Relation PSF;
	
	/**
	 * hierarchie => Ordre contenant la hiearchie des membres de PROSPEC
	 */
	private static Ordre hierarchie;

	/**
	 * aPourChef => Relation " à Pour Chef "
	 */
	private static Relation aPourChef;
	/**
	 * moinsPrio => Ordre "est moins prioritaire que" sur l'ensemble des qualifications
	 */
	private static Ordre moinsPrio;
	
	/**
	 * EST_PROCHE_DE => On dit que deux projets sont "proches" s'ils concernent une même qualification
	 */
	private static Relation EST_PROCHE_DE;
	
	/**
	 * coutSal et bonusTot sont deux variables utilisées dans la question 5. Elles servent à calculé le salaire plus rapidement.
	 */
	static double coutSal =0;
	static double bonusTot = 0;
	
	/**
	 *  L'ensemble patrons regroupe les membres du personnel n'ayant pas de supérieur.
	 */
	
	static Ensemble patrons;
	
	/**
	 * Ce tableau retient le salaire et le bonus des membres du personnel pour la question 5.3, il est rempli a la question 5
	 */
	
	static double[][] tabSal;
	
	public static void main(String[] args) throws MathException {
			

			question1();
			question2();
			question3();
			question4();
			question5();
			question6();
			question7();
	} 
	
	public static void question1(){
		System.out.println("Question 1");
		System.out.println("******************************************************************************************");
		
		System.out.println("Réponse 1.1");
		question11();
		
		System.out.println("Réponse 1.2");
		question12();
		
		System.out.println("Réponse 1.3");
		question13();
		
		System.out.println("Réponse 1.4");
		question14();
		
		System.out.println("Réponse 1.5");
		question15("Antoinet");
		
		System.out.println("Réponse 1.6");
		question16();
		
		System.out.println("Réponse 1.7");
		question17();
	}
	
	/**
	 * Membres ne participant pas à au moins un projet
	 */
	private static void question11(){
		Ensemble  ensemble = COL.depart().moins(COL.domaine());
		if(ensemble.estVide())
			System.out.println("Tous les membres du personnel collaborent au moins à un projet.");
		else
			lister(ensemble, "PERSONNELS");
	}
	/**
	 * Question 1.2
	 * Enoncé : Vérifiez que, pour chaque qualification, on peut trouver un membre du personnel compétent.
	 */
	private static void question12(){
		Ensemble  ensemble = CPT.arrivee().moins(CPT.image());
		if(ensemble.estVide())
			System.out.println("Pour toute qualification, on peut trouver un membre du personnel compétent.");
		else
			lister(ensemble, "QUALIFICATIONS");
	
	}
	/**
	 * Question 1.3
	 * Enoncé : Vérifiez que la relation Sup est acyclique.
	 */
	private static void question13(){
		if(SUP.acyclique()) System.out.println("La relation SUP est acyclique");
		else System.out.println("La relation SUP n'est pas acyclique");
	}
	
	/**
	 * Question 1.4
	 * Enoncé : Créez la relation qui lie les membres du personnel à la liste de leurs affectations possibles.
	 */
	private static void question14(){
		LAP = (CCN.reciproque()).apres(CPT);
	}
	/**
	 * Question 1.5
	 * Enoncé : Quels sont les membres du personnel qui peuvent être affectés au projet «Antoinet» ?
	 * @param projet
	 */
	private static void question15(String projet){
		System.out.println("Personnel affectable au projet "+projet);
		lister(LAP.imageReciproque(new Elt(numéro(projet, "PROJETS"))), "PERSONNELS");
	}
	/**
	 * Question 1.6
	 * Enoncé : Trouvez le(s) membre(s) du personnel qui ont le plus grand nombre d’affectations possibles.
	 */
	private static void question16(){
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
		System.out.println("Membres du personnel ayant le maximum d'affectations possibles:");
		lister(ens, "PERSONNELS");
	}
	/**
	 * Question 1.7
	 * Enoncé : 1.7	Un membre du personnel ne peut pas collaborer à un projet si celui-ci ne fait pas
	 *  partie de ses affectations possibles. Vérifiez qu’il en est bien ainsi. Si ce n’est pas le cas,
	 *  affichez la liste des membres du personnel qui ne satisfont pas la condition avec, pour chaque membre, 
	 *  la liste des projets auxquels il collabore mais ne faisant pas partie de ses affectations possibles.
	 */
	private static void question17(){
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
	
	/**
	 * Question 2
	 */
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
		
		Ensemble cat1 = new Ensemble();
		Ensemble domFIN = FIN.domaine();
		
		cat1 = FIN.depart().moins(domFIN);
		
		Ensemble cat2 = domFIN.clone();
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
		System.out.println("Aucun financement");
		lister(cat1, "PERSONNELS");
		System.out.println("Catégorie 2 : ");
		System.out.println("Aucun soutien financier lucide");
		lister(cat2, "PERSONNELS");
		System.out.println("Catégorie 3 : ");
		System.out.println("Des soutiens financiers lucides et des autres");
		lister(cat3, "PERSONNELS");
		System.out.println("Catégorie 4 : ");
		System.out.println("Que des soutiens financiers lucides");
		lister(cat4, "PERSONNELS");
		
		System.out.println("Réponse 2.2");
		Elt femme = new Elt(2);
		Iterator<Couple> itSex = SEX.iterator();

		Ensemble [] ensTab = new Ensemble[4];
		String [] message = new String[4];
		int tempCompteur = 0;
		if(!cat1.estVide()) {
			ensTab[tempCompteur] = cat1;
			message[tempCompteur] =" Catégorie 1 : Aucun financement ";
			tempCompteur++;
		}
		
		if(!cat2.estVide()) {
			ensTab[tempCompteur] = cat2;
			message[tempCompteur] ="Catégorie 2 : Aucun soutien financier lucide";
			tempCompteur++;
			
		}
		
		if(!cat3.estVide()) {
			ensTab[tempCompteur] = cat3;
			message[tempCompteur] ="Catégorie 3 : Des soutiens financiers lucides et d'autres soutiens financiers ";
			tempCompteur++;
			
		}
		
		if(!cat4.estVide()) {
			ensTab[tempCompteur] = cat4;
			message[tempCompteur] ="Catégorie 3 : Des soutiens financiers lucides et d'autres soutiens financiers"; 
			tempCompteur++;
			
		}
		
		double compteurfTab[] = new double[tempCompteur];
		
		while(itSex.hasNext()){
			Couple c = itSex.next();
		
			for(int i = 0; i<tempCompteur; i++){
				
				if(ensTab[i].contient(c.getx())){
					if(c.gety().estEgalA(femme)){
						compteurfTab[i]++;
						break;
					}
				}
		
			}
		}
		
		
		for(int i = 0; i < compteurfTab.length ; i++){
			compteurfTab[i] = compteurfTab[i]/ensTab[i].cardinal(); 
		}

		System.out.println("La/les catégorie(s) comportant la plus grande proportion de femmes est/sont : ");
		
		double max = 0;
		
		for(int i = 0; i < tempCompteur; i++){
			if(compteurfTab[i] > max){
				max = compteurfTab[i];
			}
		}
		
		for(int i = 0; i <tempCompteur; i++){
			if(compteurfTab[i] == max)
				System.out.println(message[i]);
		}
		
		
	}

	/**
	 * Question 3
	 * 
	 */
	public static void question3(){
		System.out.println("\nQuestion 3");
		System.out.println("******************************************************************************************");
		System.out.println("Question 3.1");
		question31();
		
		System.out.println("Question 3.2");
		question32("KOEKELBERG Basile");
		
		System.out.println("Question 3.3");
		question33();
	}
	/**
	 * Question 3.1
	 * Enoncé : Créez la relation « peut suivre la formation en » des membres du personnels vers les qualifications.
	 */
	private static void question31(){
		peutSuivreLaFormationEnInitialisation();
		System.out.println("peut suivre la formation en (Relation PSF) Initialisé");
	}
	
	/**
	 * Question 3.2
	 * Enoncé : Quelle formation peut suivre KOEKELBERG Basile ?
	 * @param personnel
	 */
	private static void question32(String personnel){
		System.out.println("Formation(s) que peut suivre "+ personnel +" :");
		lister(PSF.imageDirecte(numéro(personnel, "PERSONNELS")), "QUALIFICATIONS");
	}
	
	/**
	 * Question 3.3
	 * Enoncé : Y-a-t-il des membres du personnel qui ne peuvent suivre aucune formation ? Si oui, lesquels ?
	 */
	private static void question33(){
		Ensemble quest33 = new Ensemble();
		Iterator<Elt> it = PSF.depart().iterator();
		while(it.hasNext()){
			Elt el = it.next();
			if(PSF.degreDeSortie(el) == 0){
				quest33.ajouter(el);
			}
		}
		if(!quest33.estVide()){
			System.out.println("Membre(s) du personnel qui ne peut(vent) pas suivre de formation : ");
			lister(quest33,"PERSONNELS");
		}else System.out.println("Aucun membre du personnel ne doit suivre une formation");
		
	}
	
	/**
	 * Initiallisation du @param PSF 
	 * 
	 */
	private static void peutSuivreLaFormationEnInitialisation(){
		PSF = new Relation(COL.depart(), CCN.arrivee()); // les 2 clone() ont été retiré
		Iterator<Couple> itCol = COL.iterator();
		

		while(itCol.hasNext()){
			Couple couple = itCol.next();
			Elt pers = couple.getx();
			Elt projet = couple.gety(); // "y" désigne le domaine de qualification
			// Regarde les qualifications demandés pour le projet "projet"
			Ensemble qualifProjet = CCN.imageDirecte(projet);
			// Regarde les qualifications du personnel "pers"
			Ensemble qualifPers = CPT.imageDirecte(pers);
			Iterator<Elt> itQualifProjet = qualifProjet.iterator(); //Itérateur sur les qualifications du "projet"
			while(itQualifProjet.hasNext()){
				Elt x = itQualifProjet.next();
				if(!qualifPers.contient(x)){ // S'il la qualification x du projet "projet" n'est pas contenue dans l'ensemble des qualifications de "pers" 
					PSF.ajouter(pers, x); // On ajoute le personnel "pers" et la qualification "x"
				}
				
			}
			
		}
	}

	/**
	 * Question 4
	 */
	public static void question4(){
		System.out.println("\nQuestion 4");
		System.out.println("******************************************************************************************");
		
		System.out.println("Réponse 4.1 : ");
		question41();

		System.out.println("Réponse 4.2 : ");
		question42();
		
		System.out.println("Réponse 4.3 : ");
		question43();
	}
	/**
	 * Quesiton 4.1
	 * Enoncé : Créez la relation « aPourChef » de l’ensemble des projets vers l’ensemble des membres du personnel
	 */
	private static void question41(){
		aPourChef = new Relation(COL.arrivee(), COL.depart()); // On a besoin des projets avec des collaborateurs et des collaborateurs en question .
		hierarchie = new Ordre(SUP.reciproque()); // Il faut créer une hiérachie pour savoir quel membre est supérieur à un autre. On la créer avec un Ordre car ont ses méthodes seront utile.
		Iterator<Elt> it = COL.arrivee().iterator(); 
         while(it.hasNext()){
             Elt elem = it.next();
             Elt max = hierarchie.maximum(COL.imageReciproque(elem)); // Cette ligne permet de savoir qui est le chef de "elem" qui est un projet.
             if( max != null){
                 aPourChef.ajouter(elem,max); 
             }
         }
	}
	
	/**
	 * Question 4.2
	 * Enoncé : Donnez la liste des projets qui n’ont pas de chef.
	 */
	private static void question42(){
		lister(COL.arrivee().moins(aPourChef.domaine()),"PROJETS");
	}
	
	/**
	 * Question 4.3
	 * Enoncé : Existe-t-il un membre du personnel qui est chef de plusieurs projets ?
	 */
	private static void question43(){
		Iterator<Elt> itMP = aPourChef.image().iterator(); //Ensemble des chefs de chaque projet ayant un chef
		boolean chefPlPro = false; // Initialisé à false car si l'ensemble des chefs est vide il rentre dans le dernier "if" de la méthode
		while(itMP.hasNext()){
			if(aPourChef.degreDEntree(itMP.next())>1){ // Si un chef est chef de plusieurs projet, sont degré d'entrée sera plus grand que 1
				System.out.println("Il existe un membre du personnel chef de plusieurs projets");
				chefPlPro = true; // Mis à true pour ne pas rentré dans le dernier "if" de la méthode
				break;
			}else{
				chefPlPro = false;
			}
			
		}
		if(!chefPlPro) System.out.println("Il n'y a pas de membre du personnel chef de plusieurs projets");
	}
	
	/**
	 * Question 5
	 */
	public static void question5(){
		System.out.println("\nQuestion 5");
		System.out.println("******************************************************************************************");
		
		System.out.println("Réponse 5.1 : ");
		question51();
		
		System.out.println("Réponse 5.2 : ");
		question52();
	
		System.out.println("Réponse question 5.3");
		question53();
		
		System.out.println("Réponse question 5.4");
		question54();
	}
	/**
	 * Question 5.1 
	 * Enoncé : Donnez la liste des patrons.
	 */
	private static void question51(){
		System.out.println("Liste des patrons :");
		patrons = new Ensemble(SUP.depart().moins(SUP.image())); // Un patron ne sera pas dans l'ensemble image puisque personne n'est au-dessus de lui
		lister(patrons,"PERSONNELS");
	}
	
	/**
	 * Question 5.2
	 * Enoncé : Calculez le traitement des membres du personnel pour le mois de décembre.
	 */
	private static void question52(){
		
		tabSal = new double[nbPers][2]; //En tabSal[...][0] = salaire, tabSal[...][1] = bonus
		Ordre or =  new Ordre(SUP.reciproque());
		supChemin();
		Iterator<Elt> it = SUP.depart().iterator();
		while(it.hasNext()){
			Elt elem = it.next();
			int numero = elem.val();
			double salaire = BASE * Math.pow(DELTA, tableau[numero-1]-1);
			salaire += (or.minor(new Ensemble(elem)).cardinal()-1)*PRIME;
			tabSal[numero-1][0] = salaire;
			double bonus = bonus(elem);
			tabSal[numero-1][1] = bonus;
			salaire += bonus;
			System.out.println("Salaire de " +tPers[numero]+" : "+ Math.floor(salaire));
		}
	}
	
	/**
	 * Question 5.3
	 * Enoncé : Calculez le coût salarial total annuel de l’entreprise PROSPEC.
	 */
	private static void question53(){
		double coutSal=0;
		for(int i=0;i<nbPers;i++){ // On parcourt tout le tableau et on rajoute le salaire et le bonus au cout salarial
			coutSal += tabSal[i][0]*12;
			coutSal += tabSal[i][1];
		}
		System.out.println("Coût salarial total annuel de l'entreprise PROSPEC :"+ Math.floor(coutSal));
	}
	
	/**
	 * Question 5.4
	 * Enoncé : M. « BISTRO Alonzo » décide de soutenir financièrement 
	 * un projet (en plus de ceux qu’il soutient déjà). Avec les données actuelles, parmi quels projets peut-il choisir afin d’avoir le plus grand bonus ?
	 */
	private static void question54(){
		double max = 0;
		Elt bestInvest= null;
		Iterator<Elt> it = COL.arrivee().moins(COL.imageDirecte(numéro("BISTRO Alonzo","PERSONNELS"))).iterator(); // On parcourt les projets qui ne sont déjà pas financés par Monsieur BISTRO Alonzo
		while(it.hasNext()){
			Elt proj = it.next();
			double nbCollabo = COL.imageReciproque(proj).cardinal();
			if(nbCollabo != 0){
				double nbFinancier = FIN.imageReciproque(proj).cardinal() +1 ;
				if(nbFinancier == 0) nbFinancier = 1;
					double bonusCourant = (250*nbCollabo) / nbFinancier;
					if(bonusCourant > max){
						max = bonusCourant;
						bestInvest = new Elt(proj);
				}
			}
		}
		if(bestInvest==null){
			System.out.println("Aucun projet interessant !");
		}else{
			lister(new Ensemble(bestInvest), "PROJETS");
		}
	}
	
	/**
	 * Calcul le bonus du membres qu'il reçois en paramètre
	 * @param elem Est le membres pour lequel on calcul le bonus
	 * @return le bonus du elem
	 */
	private static double bonus(Elt elem){
		double bonus = 0;
		double temp=0;
		if(FIN.depart().contient(elem)){
			Ensemble proj = FIN.imageDirecte(elem);
			
			Iterator<Elt> it = proj.iterator();
			while(it.hasNext()){
				Elt el = it.next();
				temp = BONUS * COL.imageReciproque(el).cardinal();
				if(FIN.imageReciproque(el).cardinal() !=0){
					bonus += temp / (FIN.imageReciproque(el).cardinal());
				}
			}	
		}
		return bonus;
	}
	
	/**
	 * Calcul les sup chemins de tous les employés
	 * Met dans tableau le plus court sup chemin de l'employé correspondant à l'indice de ce tableau
	 */
	private static void supChemin(){
		tableau = new int[nbPers];
		int compteur = nbPers;
		int niveau = 1; // Niveau de base
		Iterator<Elt> itSup = patrons.iterator();
		while(itSup.hasNext()){ // les grands patrons
			Elt elementSup = itSup.next();
			if(tableau[elementSup.val()-1]==0){
				tableau[elementSup.val()-1]=niveau;
				compteur--;
			}
		}
		niveau++;
		while(compteur!=0){ // Les employés
			Ensemble subordonnes = SUP.imageDirecte(patrons);
			Iterator<Elt> it = subordonnes.iterator();
			while(it.hasNext()){
				Elt element = it.next();
				if(tableau[element.val()-1]==0){
					tableau[element.val()-1]=niveau;
					compteur--;
				}
			}
			niveau++;
			patrons = subordonnes;
		}
	}
	
	/**
	 * Question 6
	 */
	public static void question6(){
		System.out.println("\nQuestion 6");
		System.out.println("******************************************************************************************");
		
		System.out.println("Réponse 6.1 : ");
		question61();
		
		System.out.println("Réponse 6.2 : ");
		question62();
		
		System.out.println("Réponse 6.3 : ");
		question63();

		
		
		
		
	}
	
	/**
	 * Question 6.1
	 * Enoncé : Créez la relation « est moins prioritaire que » sur l’ensemble des qualifications.
	 */
	private static void question61(){
		moinsPrio = new Ordre(CPT.arrivee());
		Iterator<Elt> it = moinsPrio.depart().iterator();
		while(it.hasNext()){
			Elt elem = it.next();
			double priorite = (double) CCN.imageReciproque(elem).cardinal()/CPT.imageReciproque(elem).cardinal();
			Iterator<Elt> it2 = moinsPrio.depart().iterator();
			while(it2.hasNext()){
				Elt elem2 = it2.next();
				double priorite2 = (double) CCN.imageReciproque(elem2).cardinal()/CPT.imageReciproque(elem2).cardinal();
				if(priorite<priorite2){
					moinsPrio.ajouter(new Couple(elem,elem2));
				}
				if(priorite2<priorite){
					moinsPrio.ajouter(new Couple(elem2,elem));
				}
			}
		}
		
		System.out.println("Ordre initialisé !");
	}
	
	/**
	 * Question 6.2
	 * Enoncé : A priori, cet ordre est-il total ? Justifiez !
	 */
	private static void question62(){
		System.out.println("Cet ordre n'est pas total. Deux qualifications ont les mêmes proportions, elles ne sont donc pas comparables.\nPar contre, avec d'autres données, il est possible que cet ordre soit total.");
	}
	
	/**
	 * Question 6.3
	 * Enoncé : Etablissez un classement par niveau de priorité des qualifications (en commençant par les plus prioritaires).
	 */
	private static void question63(){
		int compteur = 1 ;
		Ensemble max = moinsPrio.maximaux(moinsPrio.arrivee());
		while(!max.estVide()){
			System.out.println("Priorité N°" + compteur);
			lister(max, "QUALIFICATIONS");
			Ensemble ancienmax = max;
			
			max = moinsPrio.minor(max);
			if(max==null){
				break;
			}
			max.enlever(ancienmax);
			max = moinsPrio.maximaux(max);
			
			compteur++;
		}
	}

	/**
	 * Question 7
	 */
	public static void question7(){
		System.out.println("\nQuestion 7");
		System.out.println("******************************************************************************************");
		
		System.out.println("Réponse question 7.1");
		question71();
		
		System.out.println("Réponse question 7.2");
		question72();
		
		System.out.println("Réponse question 7.3");
		question73();
	}
	
	/**
	 * Question 7.1
	 * Enoncé : Est-ce-que la relation « est proche de » est, à priori, une équivalence ? Justifiez votre réponse !
	 */
	private static void question71(){
		System.out.println("Pour qu'une relation soit une équivalence il faut qu'elle soit réfléxive, symétrique et transitive. Ce qui n'est pas le cas de la relation 'proche', qui n'est pas transitive.");
		System.out.println("Un projet A peut concerner deux qualifications D et E, si le projet B concerne la qualification D et le projet C concerne la qualification E, cela voudrait dire que les projets B et C sont proches, ce qui ne devrait pas être le cas");
	}
	
	/**
	 * Question 7.2
	 * Enoncé : Créez, sur l’ensemble des projets, la relation «est proche de». 
	 */
	private static void question72(){
		EST_PROCHE_DE = CCN.reciproque().apres(CCN); // projet à projet 
	}
	
	/**
	 * Question 7.3
	 * Donnez la liste des projets qui sont proches du projet « PAMAL ».
	 */
	private static void question73(){
		Elt pamal = new Elt(numéro("PAMAL","PROJETS"));
		Ensemble response = EST_PROCHE_DE.imageReciproque(pamal).moins(new Ensemble(pamal));//On récupère tout les projets proche de "PAMAL", et on le retire de l'ensemble car l'afficher serait inutile
		if(response !=null){
			System.out.println("Projet(s) proche(s) de PAMAL");
			lister(response, "PROJETS");
		}else{
			System.out.println("Pas de projet proche de PAMAL");
		}
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