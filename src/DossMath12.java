import java.util.Iterator;
/**
 * 
 * @author Erwan Corbisier
 * @author Mathieu Steenput
 * @title Dossier de math�matique 2012
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
	 * aPourChef => Relation " � Pour Chef "
	 */
	private static Relation aPourChef;
	/**
	 * moinsPrio => Ordre "est moins prioritaire que" sur l'ensemble des qualifications
	 */
	private static Ordre moinsPrio;
	
	/**
	 * EST_PROCHE_DE => On dit que deux projets sont "proches" s'ils concernent une m�me qualification
	 */
	private static Relation EST_PROCHE_DE;
	
	/**
	 * coutSal et bonusTot sont deux variables utilis�es dans la question 5. Elles servent � calcul� le salaire plus rapidement.
	 */
	static double coutSal =0;
	static double bonusTot = 0;
	
	/**
	 *  L'ensemble patrons regroupe les membres du personnel n'ayant pas de sup�rieur.
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
		
		System.out.println("R�ponse 1.1");
		question11();
		
		System.out.println("R�ponse 1.2");
		question12();
		
		System.out.println("R�ponse 1.3");
		question13();
		
		System.out.println("R�ponse 1.4");
		question14();
		
		System.out.println("R�ponse 1.5");
		question15("Antoinet");
		
		System.out.println("R�ponse 1.6");
		question16();
		
		System.out.println("R�ponse 1.7");
		question17();
	}
	
	/**
	 * Membres ne participant pas � au moins un projet
	 */
	private static void question11(){
		Ensemble  ensemble = COL.depart().moins(COL.domaine());
		if(ensemble.estVide())
			System.out.println("Tous les membres du personnel collaborent au moins � un projet.");
		else
			lister(ensemble, "PERSONNELS");
	}
	/**
	 * Question 1.2
	 * Enonc� : V�rifiez que, pour chaque qualification, on peut trouver un membre du personnel comp�tent.
	 */
	private static void question12(){
		Ensemble  ensemble = CPT.arrivee().moins(CPT.image());
		if(ensemble.estVide())
			System.out.println("Pour toute qualification, on peut trouver un membre du personnel comp�tent.");
		else
			lister(ensemble, "QUALIFICATIONS");
	
	}
	/**
	 * Question 1.3
	 * Enonc� : V�rifiez que la relation Sup est acyclique.
	 */
	private static void question13(){
		if(SUP.acyclique()) System.out.println("La relation SUP est acyclique");
		else System.out.println("La relation SUP n'est pas acyclique");
	}
	
	/**
	 * Question 1.4
	 * Enonc� : Cr�ez la relation qui lie les membres du personnel � la liste de leurs affectations possibles.
	 */
	private static void question14(){
		LAP = (CCN.reciproque()).apres(CPT);
	}
	/**
	 * Question 1.5
	 * Enonc� : Quels sont les membres du personnel qui peuvent �tre affect�s au projet �Antoinet� ?
	 * @param projet
	 */
	private static void question15(String projet){
		System.out.println("Personnel affectable au projet "+projet);
		lister(LAP.imageReciproque(new Elt(num�ro(projet, "PROJETS"))), "PERSONNELS");
	}
	/**
	 * Question 1.6
	 * Enonc� : Trouvez le(s) membre(s) du personnel qui ont le plus grand nombre d�affectations possibles.
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
	 * Enonc� : 1.7	Un membre du personnel ne peut pas collaborer � un projet si celui-ci ne fait pas
	 *  partie de ses affectations possibles. V�rifiez qu�il en est bien ainsi. Si ce n�est pas le cas,
	 *  affichez la liste des membres du personnel qui ne satisfont pas la condition avec, pour chaque membre, 
	 *  la liste des projets auxquels il collabore mais ne faisant pas partie de ses affectations possibles.
	 */
	private static void question17(){
		affImpossible = COL.clone();
		affImpossible.enlever(LAP);
		if(affImpossible.cardinal()!=0){
			Iterator<Couple> it = affImpossible.iterator();
			System.out.println("Liste des membres du personnel collaborant � un projet ne figurant pas dans la liste de leurs affectations possibles : ");
			while(it.hasNext()){
				Couple c = it.next();
				lister(affImpossible.imageReciproque(c.getx()), "PERSONNELS");
				System.out.println("Collaborant aux projets : ");
				lister(affImpossible.imageDirecte(c.gety()),"PROJETS");
			}
		}else{
			System.out.println("Aucun membre du personnel ne collabore � un projet ne faisant pas partie de ses affectations possibles.");
		}
	}
	
	/**
	 * Question 2
	 */
	public static void question2(){
		System.out.println("\nQuestion 2");
		System.out.println("******************************************************************************************");
		System.out.println("R�ponse 2.1");
		
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
		
		
		System.out.println("Cat�gorie 1 : ");
		System.out.println("Aucun financement");
		lister(cat1, "PERSONNELS");
		System.out.println("Cat�gorie 2 : ");
		System.out.println("Aucun soutien financier lucide");
		lister(cat2, "PERSONNELS");
		System.out.println("Cat�gorie 3 : ");
		System.out.println("Des soutiens financiers lucides et des autres");
		lister(cat3, "PERSONNELS");
		System.out.println("Cat�gorie 4 : ");
		System.out.println("Que des soutiens financiers lucides");
		lister(cat4, "PERSONNELS");
		
		System.out.println("R�ponse 2.2");
		Elt femme = new Elt(2);
		Iterator<Couple> itSex = SEX.iterator();

		Ensemble [] ensTab = new Ensemble[4];
		String [] message = new String[4];
		int tempCompteur = 0;
		if(!cat1.estVide()) {
			ensTab[tempCompteur] = cat1;
			message[tempCompteur] =" Cat�gorie 1 : Aucun financement ";
			tempCompteur++;
		}
		
		if(!cat2.estVide()) {
			ensTab[tempCompteur] = cat2;
			message[tempCompteur] ="Cat�gorie 2 : Aucun soutien financier lucide";
			tempCompteur++;
			
		}
		
		if(!cat3.estVide()) {
			ensTab[tempCompteur] = cat3;
			message[tempCompteur] ="Cat�gorie 3 : Des soutiens financiers lucides et d'autres soutiens financiers ";
			tempCompteur++;
			
		}
		
		if(!cat4.estVide()) {
			ensTab[tempCompteur] = cat4;
			message[tempCompteur] ="Cat�gorie 3 : Des soutiens financiers lucides et d'autres soutiens financiers"; 
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

		System.out.println("La/les cat�gorie(s) comportant la plus grande proportion de femmes est/sont : ");
		
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
	 * Enonc� : Cr�ez la relation � peut suivre la formation en � des membres du personnels vers les qualifications.
	 */
	private static void question31(){
		peutSuivreLaFormationEnInitialisation();
		System.out.println("peut suivre la formation en (Relation PSF) Initialis�");
	}
	
	/**
	 * Question 3.2
	 * Enonc� : Quelle formation peut suivre KOEKELBERG Basile ?
	 * @param personnel
	 */
	private static void question32(String personnel){
		System.out.println("Formation(s) que peut suivre "+ personnel +" :");
		lister(PSF.imageDirecte(num�ro(personnel, "PERSONNELS")), "QUALIFICATIONS");
	}
	
	/**
	 * Question 3.3
	 * Enonc� : Y-a-t-il des membres du personnel qui ne peuvent suivre aucune formation ? Si oui, lesquels ?
	 */
	private static void question33(){
		Ensemble quest33 = new Ensemble();
		Iterator<Elt> it = PSF.depart().iterator();
		while(it.hasNext()){
			Elt el = it.next();
			if(PSF.degreDeSortie(el) == 0){ // Si il ne suit pas de formation
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
		PSF = new Relation(COL.depart(), CCN.arrivee()); // les 2 clone() ont �t� retir�
		Iterator<Couple> itCol = COL.iterator();
		

		while(itCol.hasNext()){
			Couple couple = itCol.next();
			Elt pers = couple.getx();
			Elt projet = couple.gety(); // "y" d�signe le domaine de qualification
			// Regarde les qualifications demand�s pour le projet "projet"
			Ensemble qualifProjet = CCN.imageDirecte(projet);
			// Regarde les qualifications du personnel "pers"
			Ensemble qualifPers = CPT.imageDirecte(pers);
			Iterator<Elt> itQualifProjet = qualifProjet.iterator(); //It�rateur sur les qualifications du "projet"
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
		
		System.out.println("R�ponse 4.1 : ");
		question41();

		System.out.println("R�ponse 4.2 : ");
		question42();
		
		System.out.println("R�ponse 4.3 : ");
		question43();
	}
	/**
	 * Quesiton 4.1
	 * Enonc� : Cr�ez la relation � aPourChef � de l�ensemble des projets vers l�ensemble des membres du personnel
	 */
	private static void question41(){
		aPourChef = new Relation(COL.arrivee(), COL.depart()); // On a besoin des projets avec des collaborateurs et des collaborateurs en question .
		hierarchie = new Ordre(SUP.reciproque()); // Il faut cr�er une hi�rachie pour savoir quel membre est sup�rieur � un autre. On la cr�er avec un Ordre car ont ses m�thodes seront utile.
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
	 * Enonc� : Donnez la liste des projets qui n�ont pas de chef.
	 */
	private static void question42(){
		lister(COL.arrivee().moins(aPourChef.domaine()),"PROJETS");
	}
	
	/**
	 * Question 4.3
	 * Enonc� : Existe-t-il un membre du personnel qui est chef de plusieurs projets ?
	 */
	private static void question43(){
		Iterator<Elt> itMP = aPourChef.image().iterator(); //Ensemble des chefs de chaque projet ayant un chef
		boolean chefPlPro = false; // Initialis� � false car si l'ensemble des chefs est vide il rentre dans le dernier "if" de la m�thode
		while(itMP.hasNext()){
			if(aPourChef.degreDEntree(itMP.next())>1){ // Si un chef est chef de plusieurs projet, sont degr� d'entr�e sera plus grand que 1
				System.out.println("Il existe un membre du personnel chef de plusieurs projets");
				chefPlPro = true; // Mis � true pour ne pas rentr� dans le dernier "if" de la m�thode
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
		
		System.out.println("R�ponse 5.1 : ");
		question51();
		
		System.out.println("R�ponse 5.2 : ");
		question52();
	
		System.out.println("R�ponse question 5.3");
		question53();
		
		System.out.println("R�ponse question 5.4");
		question54();
	}
	/**
	 * Question 5.1 
	 * Enonc� : Donnez la liste des patrons.
	 */
	private static void question51(){
		System.out.println("Liste des patrons :");
		patrons = new Ensemble(SUP.depart().moins(SUP.image())); // Un patron ne sera pas dans l'ensemble image puisque personne n'est au-dessus de lui
		lister(patrons,"PERSONNELS");
	}
	
	/**
	 * Question 5.2
	 * Enonc� : Calculez le traitement des membres du personnel pour le mois de d�cembre.
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
	 * Enonc� : Calculez le co�t salarial total annuel de l�entreprise PROSPEC.
	 */
	private static void question53(){
		double coutSal=0;
		for(int i=0;i<nbPers;i++){ // On parcourt tout le tableau et on rajoute le salaire et le bonus au cout salarial
			coutSal += tabSal[i][0]*12;
			coutSal += tabSal[i][1];
		}
		System.out.println("Co�t salarial total annuel de l'entreprise PROSPEC :"+ Math.floor(coutSal));
	}
	
	/**
	 * Question 5.4
	 * Enonc� : M. � BISTRO Alonzo � d�cide de soutenir financi�rement 
	 * un projet (en plus de ceux qu�il soutient d�j�). Avec les donn�es actuelles, parmi quels projets peut-il choisir afin d�avoir le plus grand bonus ?
	 */
	private static void question54(){
		double max = 0;
		Elt bestInvest= null;
		Iterator<Elt> it = COL.arrivee().moins(COL.imageDirecte(num�ro("BISTRO Alonzo","PERSONNELS"))).iterator(); // On parcourt les projets qui ne sont d�j� pas financ�s par Monsieur BISTRO Alonzo
		while(it.hasNext()){
			Elt proj = it.next();
			double nbCollabo = COL.imageReciproque(proj).cardinal();
			if(nbCollabo != 0){
				double nbFinancier = FIN.imageReciproque(proj).cardinal() +1 ;
				double bonusCourant = (250*nbCollabo) / nbFinancier;
				if(bonusCourant > max){ // Si le bonus apport� par le projet est plus grand que l'ancien
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
	 * Calcul le bonus du membres qu'il re�ois en param�tre
	 * @param elem Est le membres pour lequel on calcul le bonus
	 * @return le bonus du elem
	 */
	private static double bonus(Elt elem){
		double bonus = 0; // Bonus total de elem
		double temp=0; // Bonus temporaire d'un des projets soutenu par elem
		if(FIN.depart().contient(elem)){ 
			Ensemble proj = FIN.imageDirecte(elem); // On r�cup�re l'ensemble des projets financer par elem
			
			Iterator<Elt> it = proj.iterator();
			while(it.hasNext()){
				Elt el = it.next();
				temp = BONUS * COL.imageReciproque(el).cardinal(); // Pour chaque projet, on regarde le nombre de collabo qu'on multiplie par BONUS
				if(FIN.imageReciproque(el).cardinal() !=0){
					bonus += temp / (FIN.imageReciproque(el).cardinal()); // on divise le bonus de el par le nombre de financier du projet
				}
			}	
		}
		return bonus;
	}
	
	/**
	 * Calcul les sup chemins de tous les employ�s
	 * Met dans tableau le plus court sup chemin de l'employ� correspondant � l'indice de ce tableau
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
		while(compteur!=0){ // Les employ�s
			Ensemble subordonnes = SUP.imageDirecte(patrons);
			Iterator<Elt> it = subordonnes.iterator();
			while(it.hasNext()){
				Elt element = it.next();
				if(tableau[element.val()-1]==0){ // Si un sup chemin plus court n'a pas �t� trouv� pour cet employ�
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
		
		System.out.println("R�ponse 6.1 : ");
		question61();
		
		System.out.println("R�ponse 6.2 : ");
		question62();
		
		System.out.println("R�ponse 6.3 : ");
		question63();

		
		
		
		
	}
	
	/**
	 * Question 6.1
	 * Enonc� : Cr�ez la relation � est moins prioritaire que � sur l�ensemble des qualifications.
	 * est moins prioritaire que : Une qualification est class�e moins prioritaire qu'une autre si la proportion entre
	 * le nombre de projet concernant cette qualification et le nombre de collaborateurs comp�tent dans cette 
	 * qualification est strictement inf�rieure � celle de l'autre
	 */
	private static void question61(){
		moinsPrio = new Ordre(CPT.arrivee()); // CPT.arrivee() => Ensemble des qualifications 
		Iterator<Elt> it = moinsPrio.depart().iterator();
		while(it.hasNext()){
			Elt qualif = it.next(); 
			double priorite = (double) CCN.imageReciproque(qualif).cardinal()/CPT.imageReciproque(qualif).cardinal(); // projet(s) / personnel(s)
			Iterator<Elt> it2 = moinsPrio.depart().iterator();
			while(it2.hasNext()){
				Elt qualif2 = it2.next();
				double priorite2 = (double) CCN.imageReciproque(qualif2).cardinal()/CPT.imageReciproque(qualif2).cardinal(); //projet(s) / personnel(s)
				if(priorite<priorite2){ //qualif est moins prioritaire que qualif2
					moinsPrio.ajouter(new Couple(qualif,qualif2));
				}else {
					if(priorite2<priorite){ // qualif2 est moins prioritaire que qualif
						moinsPrio.ajouter(new Couple(qualif2,qualif));
					}
				}
			}
		}
		
		System.out.println("Ordre initialis� !");
	}
	
	/**
	 * Question 6.2
	 * Enonc� : A priori, cet ordre est-il total ? Justifiez !
	 */
	private static void question62(){
		System.out.println("Cet ordre n'est pas total. Deux qualifications ont les m�mes proportions, elles ne sont donc pas comparables.\nPar contre, avec d'autres donn�es, il est possible que cet ordre soit total.");
	}
	
	/**
	 * Question 6.3
	 * Enonc� : Etablissez un classement par niveau de priorit� des qualifications (en commen�ant par les plus prioritaires).
	 */
	private static void question63(){
		int compteur = 1 ; //Le compteur affichera les niveaux. Il est initialis� � 1 car le niveau 0 n'existe pas
		Ensemble max = moinsPrio.maximaux (moinsPrio.arrivee());
		while(!max.estVide()){
			System.out.println("Priorit� N�" + compteur);
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
		
		System.out.println("R�ponse question 7.1");
		question71();
		
		System.out.println("R�ponse question 7.2");
		question72();
		
		System.out.println("R�ponse question 7.3");
		question73();
	}
	
	/**
	 * Question 7.1
	 * Enonc� : Est-ce-que la relation � est proche de � est, � priori, une �quivalence ? Justifiez votre r�ponse !
	 */
	private static void question71(){
		System.out.println("Pour qu'une relation soit une �quivalence il faut qu'elle soit r�fl�xive, sym�trique et transitive. Ce qui n'est pas le cas de la relation 'proche', qui n'est pas transitive.");
		System.out.println("Un projet A peut concerner deux qualifications D et E, si le projet B concerne la qualification D et le projet C concerne la qualification E, cela voudrait dire que les projets B et C sont proches, ce qui ne devrait pas �tre le cas");
	}
	
	/**
	 * Question 7.2
	 * Enonc� : Cr�ez, sur l�ensemble des projets, la relation �est proche de�. 
	 */
	private static void question72(){
		EST_PROCHE_DE = CCN.reciproque().apres(CCN); // projet � projet 
	}
	
	/**
	 * Question 7.3
	 * Donnez la liste des projets qui sont proches du projet � PAMAL �.
	 */
	private static void question73(){
		Elt pamal = new Elt(num�ro("PAMAL","PROJETS"));
		Ensemble response = EST_PROCHE_DE.imageReciproque(pamal).moins(new Ensemble(pamal));//On r�cup�re tout les projets proche de "PAMAL", et on le retire de l'ensemble car l'afficher serait inutile
		if(response !=null){
			System.out.println("Projet(s) proche(s) de PAMAL");
			lister(response, "PROJETS");
		}else{
			System.out.println("Pas de projet proche de PAMAL");
		}
	}

	// affiche � l'�cran les �l�ments de e, interpr�t�s selon contexte
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

	// renvoie le Elt correpondant � nom, d'apr�s contexte; null si incorrect
	private static Elt num�ro(String nom, String contexte) throws MathException {
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
	} // num�ro

} // class