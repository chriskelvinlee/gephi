/*
Copyright 2008 WebAtlas
Authors : Mathieu Bastian, Mathieu Jacomy, Julian Bilcke
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.data.network.potato;

import org.gephi.data.network.node.PreNode;
import org.gephi.graph.api.Node;

/**
 *
 * @author Loïc Fricoteaux
 * @author Mathieu Bastian
 */
public class PotatoRender {

    private Potato potato;
    //Config
    private float RAYON_INFLUENCE_MAX;
    private int NB_SUBDIVISIONS;
    private float INFLUENCE;
    private float RAYON;
    private double INTER;

    public PotatoRender() {
        loadConfig();
    }

    private void loadConfig() {
        NB_SUBDIVISIONS=2;
        RAYON=0.5f;
        INFLUENCE=0.79f;
        RAYON_INFLUENCE_MAX=1.5f;
        INTER = RAYON * Math.sqrt(2.0) / NB_SUBDIVISIONS;
    }

    //===========
    private double left(int i, double origine_i) {
        return i * INTER + origine_i;
    }

    private double bottom(int j, double origine_j) {
        return j * INTER + origine_j;
    }

    private double top(double jpsensj, double origine_j) {
        return jpsensj * INTER + origine_j;
    }

    private double right(double ipsensi, double origine_i) {
        return ipsensi * INTER + origine_i;
    }
    //===========

    private double equationSphere(Node node, double px, double py) {
        return Math.sqrt((px - node.x()) * (px - node.x()) + (py - node.y()) * (py - node.y()));
    }

    private double fonctionPotentielle(double distanceDisque) {
        //Fonction Blinn (non bornée)
        //return exp(-distanceDisque/10);

        //Fonction Murakami (bornée)
        if (distanceDisque > RAYON_INFLUENCE_MAX) {
            return 0;
        } else {
            double rapportDistance = distanceDisque / RAYON_INFLUENCE_MAX;
            return (1 - rapportDistance * rapportDistance) * (1 - rapportDistance * rapportDistance);
        }
    }

    /* fonction qui calcule le "poids" d'un point sur la grille */
    private double calculBlob(double posx, double posy, Node node) {

        //Optimisation : on vérifie d'abord si le point est en dehors du disque déjà tracé
        double val = 0, distanceSphere, distanceSphereOrigine;
        distanceSphereOrigine = equationSphere(node, posx, posy);
        if (distanceSphereOrigine > RAYON_INFLUENCE_MAX) //le point est hors de l'influence du disque
        {
            return -1;
        } else {
            val = fonctionPotentielle(distanceSphereOrigine);
        }

        for (PreNode pn : potato.getContent()) {
            Node node2 = pn.getNode();
            if (node != node2) {
                distanceSphere = equationSphere(node2, posx, posy);
                val += fonctionPotentielle(distanceSphere);
            }
        }

        return val;
    }

    /* calcul de l'interpolation lineaire entre 2 points */
    private double inter_lin(double Fp, double Fq) {
        return (INFLUENCE - Fp) / (Fq - Fp);
    }

    public void cookPotato(Potato potato) {
        this.potato = potato;
        for (PreNode pn : potato.getContent()) {
            Node node = pn.getNode();

            //DESSINE LE DISQUE MINIMAL AUTOUR DU NOEUD
            createCircle().circle(node.x(), node.y(), RAYON);


            //DESSINE LA ZONE D'INFLUENCE AUTOUR DU NOEUD
            for (int cote = 0; cote < 4; cote++) {
                double origine_i, origine_j, step_i, step_j, sens_i, sens_j;
                int g_tailleGrille = NB_SUBDIVISIONS;

                if (cote == 0) //HAUT
                {
                    origine_i = node.x() - RAYON * Math.sqrt(2.0) / 2.0;
                    origine_j = node.y();// + RAYON*sqrt(2.0)/2.0;
                    step_i = -INTER;
                    step_j = INTER;
                    sens_i = 1;
                    sens_j = 1;
                } else if (cote == 1) //BAS
                {
                    origine_i = node.x() - RAYON * Math.sqrt(2.0) / 2.0;
                    origine_j = node.y() - RAYON * Math.sqrt(2.0) / 2.0;
                    step_i = -INTER;
                    step_j = -INTER;
                    sens_i = 1;
                    //sens_j = -1;
                    sens_j = 1;
                } else if (cote == 2) //GAUCHE
                {
                    origine_i = node.x() - RAYON * Math.sqrt(2.0) / 2.0;
                    origine_j = node.y() - RAYON * Math.sqrt(2.0) / 2.0;
                    step_i = -INTER;
                    step_j = -INTER;
                    //sens_i = -1;
                    sens_i = 1;
                    sens_j = 1;
                } else //DROITE
                {
                    origine_i = node.x() + RAYON * Math.sqrt(2.0) / 2.0;
                    origine_j = node.y() - RAYON * Math.sqrt(2.0) / 2.0;
                    step_i = INTER;
                    step_j = -INTER;
                    sens_i = 1;
                    sens_j = 1;
                }

                boolean finCote = false;

                while (!finCote) {
                    finCote = true;

                    boolean bl = false, br = false, tr = false, tl = false; /* booleens sur les 4 sommets du carré*/
                    double fbl, fbr, ftr, ftl; /* poids de chaque sommet */
                    float p1x, p1y, p2x, p2y, p3x, p3y, p4x, p4y; /* valeur des points "intermédaires" sur les cotés du carré */

                    int actifs;

                    for (int k = 0; k < g_tailleGrille; k++) {
                        int i = 0, j = 0;

                        if (cote == 0 || cote == 1) {
                            i = k;
                        } else {
                            j = k;
                        }

                        double LEFT = left(i, origine_i);
                        double BOTTOM = bottom(j, origine_j);
                        double RIGHT = right(i + sens_i, origine_i);
                        double TOP = top(j + sens_j, origine_j);


                        /* on calcule le poids de chaque sommet du carré*/
                        fbl = calculBlob(LEFT, BOTTOM, node);
                        if (fbl >= INFLUENCE) {
                            bl = true;
                        }

                        fbr = calculBlob(RIGHT, BOTTOM, node);
                        if (fbr >= INFLUENCE) {
                            br = true;
                        }

                        ftr = calculBlob(RIGHT, TOP, node);
                        if (ftr >= INFLUENCE) {
                            tr = true;
                        }

                        ftl = calculBlob(LEFT, TOP, node);
                        if (ftl >= INFLUENCE) {
                            tl = true;
                        }

                        if (!bl && !br && !tr && !tl) //aucune influence, il ne faut rien colorier
                        {
                            continue;
                        } else if ((!bl || (bl && fbl == -1)) && (!br || (br && fbr == -1)) && (!tr || (tr && ftr == -1)) && (!tl || (tl && ftl == -1))) //déjà colorié
                        {
                            continue;
                        }
                        finCote = false;

                        actifs = (bl ? 1 : 0) + (br ? 1 : 0) + (tr ? 1 : 0) + (tl ? 1 : 0);

                        if (actifs == 0) {
                            continue;
                        }

                        /*conversion float*/
                        float LEFTf = (float) LEFT;
                        float TOPf = (float) TOP;
                        float RIGHTf = (float) RIGHT;
                        float BOTTOMf = (float) BOTTOM;

                        /* calcul des positions intermédiares */
                        p1x = LEFTf;
                        p1y = (float) (j * INTER + INTER * inter_lin(fbl, ftl) + origine_j);
                        p2x = (float) (i * INTER + INTER * inter_lin(fbl, fbr) + origine_i);
                        p2y = BOTTOMf;
                        p3x = RIGHTf;
                        p3y = (float) (j * INTER + INTER * inter_lin(fbr, ftr) + origine_j);
                        p4x = (float) (i * INTER + INTER * inter_lin(ftl, ftr) + origine_i);
                        p4y = TOPf;

                        /* En fonction des poids de chaque sommet on dessine le carré différement*/
                        if (actifs == 4) {
                            /* Les 4 sommets dedans*/
                            createSquare().square(LEFTf, BOTTOMf, RIGHTf, BOTTOMf, RIGHTf, TOPf, LEFTf, TOPf);
                        } else if (actifs == 3) {
                            /* 3 points dedans */
                            /* dessin d'un carré avec une corne en moins  */
                            /* (utilisation des valeurs intermédiares calculées avant */

                            if (!bl) {
                                //Fan
                                Potato.TriangleFan fan = createTriangleFan(3);
                                fan.triangle(p1x, p1y, p2x, p2y, RIGHTf, BOTTOMf);
                                fan.triangle(p1x, p1y, RIGHTf, BOTTOMf, RIGHTf, TOPf);
                                fan.triangle(p1x, p1y, RIGHTf, TOPf, LEFTf, TOPf);

                            } else if (!br) {
                                //Fan
                                Potato.TriangleFan fan = createTriangleFan(3);
                                fan.triangle(LEFTf, BOTTOMf, p2x, p2y, p3x, p3y);
                                fan.triangle(LEFTf, BOTTOMf, p3x, p3y, RIGHTf, TOPf);
                                fan.triangle(LEFTf, BOTTOMf, RIGHTf, TOPf, LEFTf, TOPf);

                            } else if (!tr) {
                                //Fan
                                Potato.TriangleFan fan = createTriangleFan(3);
                                fan.triangle(p3x, p3y, p4x, p4y, LEFTf, TOPf);
                                fan.triangle(p3x, p3y, LEFTf, TOPf, LEFTf, BOTTOMf);
                                fan.triangle(p3x, p3y, LEFTf, BOTTOMf, RIGHTf, BOTTOMf);

                            } else if (!tl) {
                                //Fan
                                Potato.TriangleFan fan = createTriangleFan(3);
                                fan.triangle(p4x, p4y, p1x, p1y, LEFTf, BOTTOMf);
                                fan.triangle(p4x, p4y, LEFTf, BOTTOMf, RIGHTf, BOTTOMf);
                                fan.triangle(p4x, p4y, RIGHTf, BOTTOMf, RIGHTf, TOPf);

                            }


                        } else if (actifs == 2) { /* 2 points d'activé*/
                            if (bl == tl) {
                                if (bl) {
                                    createSquare().square(p2x, p2y, p4x, p4y, LEFTf, TOPf, LEFTf, BOTTOMf);

                                } else {
                                    createSquare().square(p2x, p2y, RIGHTf, BOTTOMf, RIGHTf, TOPf, p4x, p4y);

                                }
                            } else if (bl == br) {
                                if (bl) {
                                    createSquare().square(LEFTf, BOTTOMf, RIGHTf, BOTTOMf, p3x, p3y, p1x, p1y);

                                } else {
                                    createSquare().square(p3x, p3y, RIGHTf, TOPf, LEFTf, TOPf, p1x, p1y);

                                }
                            } else {
                                if (bl == tr && !bl) {
                                    //Fan
                                    Potato.TriangleFan fan = createTriangleFan(4);
                                    fan.triangle(p1x, p1y, p2x, p2y, RIGHTf, BOTTOMf);
                                    fan.triangle(p1x, p1y, RIGHTf, BOTTOMf, p3x, p3y);
                                    fan.triangle(p1x, p1y, p3x, p3y, p4x, p4y);
                                    fan.triangle(p1x, p1y, p4x, p4y, LEFTf, TOPf);

                                } else {
                                    //Fan
                                    Potato.TriangleFan fan = createTriangleFan(4);
                                    fan.triangle(LEFTf, BOTTOMf, p2x, p2y, p3x, p3y);
                                    fan.triangle(LEFTf, BOTTOMf, p3x, p3y, RIGHTf, TOPf);
                                    fan.triangle(LEFTf, BOTTOMf, RIGHTf, TOPf, p4x, p4y);
                                    fan.triangle(LEFTf, BOTTOMf, p4x, p4y, p1x, p1y);

                                }
                            }
                        } else if (actifs == 1) {
                            /* juste un sommet dans le blob*/
                            if (bl) {
                                createTriangle().triangle(p1x, p1y, LEFTf, BOTTOMf, p2x, p2y);

                            } else if (br) {
                                createTriangle().triangle(p2x, p2y, RIGHTf, BOTTOMf, p3x, p3y);

                            } else if (tr) {

                                createTriangle().triangle(p3x, p3y, RIGHTf, TOPf, p4x, p4y);

                            } else if (tl) {
                                createTriangle().triangle(p4x, p4y, LEFTf, TOPf, p1x, p1y);

                            }
                        }
                    }

                    origine_i += step_i;
                    origine_j += step_j;
                    g_tailleGrille += 2;
                }
            }
        }
    }


    public Potato.TriangleFan createTriangleFan(int size)
    {
        Potato.TriangleFan t = new Potato.TriangleFan(size);
        potato.trianglesFan.add(t);
        return t;
    }

    public Potato.Triangle createTriangle()
    {
        Potato.Triangle t= new Potato.Triangle();
        potato.triangles.add(t);
        return t;
    }

    public Potato.Square createSquare()
    {
        Potato.Square s = new Potato.Square();
        potato.squares.add(s);
        return s;
    }

    public Potato.Circle createCircle()
    {
        Potato.Circle c = new Potato.Circle();
        potato.circles.add(c);
        return c;
    }
}
