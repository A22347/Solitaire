
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.awt.event.*;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

public class GUI extends JPanel {
	
	protected Solitaire game = null;
	
	static final int WINDOW_WIDTH = 1000;
	static final int WINDOW_HEIGHT = 700;
	static final int CARD_WIDTH = 105;
	static final int CARD_HEIGHT = 150;
	static final int CARD_BORDER = 6;
	static final int DRAW_X_POS = 25;
	static final int DRAW_Y_POS = 20;
	static final int TABLEAU_X_POS = 20;
	static final int TABLEAU_Y_POS = 200;
	static final int TABLEAU_DISTANCE = 137;
	static final int FOUNDATION_X_POS = TABLEAU_X_POS + 3 * TABLEAU_DISTANCE;
	static final int FOUNDATION_Y_POS = DRAW_Y_POS;
	static final int TABLEAU_Y_DISTANCE = 18;
	static final int DEAL_3_SHIFT_DIST = 30;
	
	protected int recentMouseX;
	protected int recentMouseY;
	protected int recentCardX;
	protected int recentCardY;
	
	protected TexturePaint cardBackPaint;
	
	class CardClickInfo {
	    public final int tableau;
	    public final int cards;
	    public final int cardX;
	    public final int cardY;

	    public CardClickInfo(int _tableau, int _cards, int _cardX, int _cardY) {
	    	tableau = _tableau;
	    	cards = _cards;
	    	cardX = _cardX;
	    	cardY = _cardY;
	    }
	}
	
	protected void drawBackOfCard(Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g;

		g.setColor(new Color(0xFFFFFF));
		g.fillRect(x, y, CARD_WIDTH, CARD_HEIGHT);
		
		g.setColor(new Color(0x000000));
		g.drawRect(x, y, CARD_WIDTH - 1, CARD_HEIGHT - 1);
		
		g.setColor(new Color(0x0040C0));
		g.fillRect(x + CARD_BORDER, y + CARD_BORDER, CARD_WIDTH - 2 * CARD_BORDER, CARD_HEIGHT - 2 * CARD_BORDER);
		
		g2.setPaint(cardBackPaint);
		g2.fillRect(x + CARD_BORDER * 2, y + CARD_BORDER * 2, CARD_WIDTH - 4 * CARD_BORDER, CARD_HEIGHT - 4 * CARD_BORDER);
	}
	
	protected void drawSymbol(Graphics g, int x, int y, boolean upsideDown, Card.Suit suit) {
		if (suit == Card.Suit.Diamond) {
			int xpoly[] = {x, x - 8, x, x + 8};
			int ypoly[] = {y - 12, y, y + 12, y};
			g.fillPolygon(new Polygon(xpoly, ypoly, xpoly.length));
			
		} else if (suit == Card.Suit.Heart) {
			if (upsideDown) {
				int xpoly[] = {x, x - 8, x, x + 8};
				int ypoly[] = {y, y, y - 12, y};
				g.fillPolygon(new Polygon(xpoly, ypoly, xpoly.length));
				g.fillOval(x - 9, y - 3, 10, 10);
				g.fillOval(x - 1, y - 3, 10, 10);
				
			} else {
				int xpoly[] = {x, x - 8, x, x + 8};
				int ypoly[] = {y, y, y + 12, y};
				g.fillPolygon(new Polygon(xpoly, ypoly, xpoly.length));
				g.fillOval(x - 9, y - 8, 10, 10);
				g.fillOval(x - 1, y - 8, 10, 10);
			}
		

		} else if (suit == Card.Suit.Spade) {
			if (upsideDown) {
				int xpoly[] = {x, x - 8, x, x + 8};
				int ypoly[] = {y, y, y + 12, y};
				g.fillPolygon(new Polygon(xpoly, ypoly, xpoly.length));
				g.fillOval(x - 9, y - 8, 10, 10);
				g.fillOval(x - 1, y - 8, 10, 10);
				
				g.fillRect(x, y - 10, 1, 10);
				g.fillRect(x - 1, y - 10, 3, 3);
				g.fillRect(x - 3, y - 10, 7, 1);
				
			} else {
				int xpoly[] = {x, x - 8, x, x + 8};
				int ypoly[] = {y, y, y - 12, y};
				g.fillPolygon(new Polygon(xpoly, ypoly, xpoly.length));
				g.fillOval(x - 9, y - 3, 10, 10);
				g.fillOval(x - 1, y - 3, 10, 10);
				g.fillRect(x, y, 1, 10);
				g.fillRect(x - 1, y + 7, 3, 3);
				g.fillRect(x - 3, y + 9, 7, 1);

				
			}
			
		} else {
			if (upsideDown) {
				g.fillOval(x - 5, y - 1, 10, 10);
				g.fillOval(x - 9, y - 8, 10, 10);
				g.fillOval(x - 1, y - 8, 10, 10);
				
				g.fillRect(x, y - 10, 1, 10);
				g.fillRect(x - 1, y - 10, 3, 3);
				g.fillRect(x - 3, y - 10, 7, 1);
				
			} else {
				g.fillOval(x - 5, y - 10, 10, 10);
				g.fillOval(x - 9, y - 3, 10, 10);
				g.fillOval(x - 1, y - 3, 10, 10);
				g.fillRect(x, y, 1, 10);
				g.fillRect(x - 1, y + 7, 3, 3);
				g.fillRect(x - 3, y + 9, 7, 1);
			}
			
		}
		
	}
	
	protected void drawCard(Graphics g, int x, int y, Card card, boolean showing) {
		if (!showing) {
			drawBackOfCard(g, x, y);
			return;
		}
				
		g.setColor(new Color(0xFFFFFF));
		g.fillRect(x, y, CARD_WIDTH, CARD_HEIGHT);
		
		g.setColor(new Color(0x000000));
		g.drawRect(x, y, CARD_WIDTH - 1, CARD_HEIGHT - 1);
		
		String rank = card.rank == Card.RANK_ACE   ? "A" : 
					  card.rank == Card.RANK_JACK  ? "J" : 
				      card.rank == Card.RANK_QUEEN ? "Q" :
					  card.rank == Card.RANK_KING  ? "K" : String.format("%d", card.rank);
		
		g.setColor(new Color(card.isBlack() ? 0x000000 : 0xFF0000));
		if (card.rank == 10) {
			g.setFont(new Font("Courier New", Font.BOLD, 22));
			g.drawString("1", x + 4, y + 22);
			g.drawString("0", x + 12, y + 22);
			g.setFont(new Font("Courier New", Font.BOLD, -22));
			g.drawString("0", x + CARD_WIDTH - 7 - 5, y + CARD_HEIGHT - 22);
			g.drawString("1", x + CARD_WIDTH - 7 + 3, y + CARD_HEIGHT - 22);
			
		} else {
			g.setFont(new Font("Courier New", Font.BOLD, 22));
			g.drawString(rank, x + 5, y + 22);
			g.setFont(new Font("Courier New", Font.BOLD, -22));
			g.drawString(rank, x + CARD_WIDTH - 5, y + CARD_HEIGHT - 22);
		}
		
		
		if (card.rank == 1) {
			if (card.suit == Card.Suit.Spade) {
				int xpoly[] = {x + CARD_WIDTH / 2, x + CARD_WIDTH / 2 - 27, x + CARD_WIDTH / 2, x + CARD_WIDTH / 2 + 27};
				int ypoly[] = {y + CARD_HEIGHT / 2, y + CARD_HEIGHT / 2, y - 36 + CARD_HEIGHT / 2, y + CARD_HEIGHT / 2};
				g.fillPolygon(new Polygon(xpoly, ypoly, xpoly.length));
				g.fillOval(x + CARD_WIDTH / 2 - 27, y + CARD_HEIGHT / 2 - 10, 30, 30);
				g.fillOval(x + CARD_WIDTH / 2 - 3, y + CARD_HEIGHT / 2 - 10, 30, 30);
				g.fillRect(x + CARD_WIDTH / 2, y + CARD_HEIGHT / 2, 3, 30);
				
				int xpoly2[] = {x + CARD_WIDTH / 2 + 1, x + CARD_WIDTH / 2 - 19, x + CARD_WIDTH / 2 + 21};
				int ypoly2[] = {y + CARD_HEIGHT / 2 + 20, y + CARD_HEIGHT / 2 + 30, y + CARD_HEIGHT / 2 + 30};
				g.fillPolygon(new Polygon(xpoly2, ypoly2, xpoly2.length));
				
				g.setFont(new Font("Courier New", Font.BOLD, 8));
				g.drawString("N.INGLIS", x + CARD_WIDTH / 2 - 18, y + CARD_WIDTH / 2 + 62);
				
			} else {
				drawSymbol(g, x + CARD_WIDTH / 2, y + CARD_HEIGHT / 2, false, card.suit);
			}
			
		} else if (card.rank == 2){
			drawSymbol(g, x + CARD_WIDTH / 2, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 3, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH / 2, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 3, true, card.suit);
		
		} else if (card.rank == 3) {
			drawSymbol(g, x + CARD_WIDTH / 2, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 3, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH / 2, y + CARD_HEIGHT / 2, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH / 2, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 3, true, card.suit);
		
		} else if (card.rank == 4) {
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 3, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 3, true, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 3, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 3, true, card.suit);
		
		} else if (card.rank == 5) {
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 3, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 3, true, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 3, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 3, true, card.suit);
		
			drawSymbol(g, x + CARD_WIDTH / 2, y + CARD_HEIGHT / 2, false, card.suit);

		} else if (card.rank == 6) {
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 3, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 3, true, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 3, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 3, true, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2, false, card.suit);
		
		} else if (card.rank == 7) {
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 3, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 3, true, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 3, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 3, true, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2, false, card.suit);
			
			drawSymbol(g, x + CARD_WIDTH / 2, y + CARD_HEIGHT * 2 / 3, true, card.suit);
		
		} else if (card.rank == 8) {
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 3, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 3, true, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 3, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 3, true, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2, false, card.suit);
			
			drawSymbol(g, x + CARD_WIDTH / 2, y + CARD_HEIGHT / 3, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH / 2, y + CARD_HEIGHT * 2 / 3, true, card.suit);
		
		} else if (card.rank == 9) {
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 3, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 9, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 9, true, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 3, true, card.suit);
			
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 3, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 9, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 9, true, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 3, true, card.suit);
			
			drawSymbol(g, x + CARD_WIDTH / 2, y + CARD_HEIGHT / 2, false, card.suit);
		
		} else if (card.rank == 10) {
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 3, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 9, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 9, true, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 3, true, card.suit);
			
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 3, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 9, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 9, true, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 3, true, card.suit);
			
			drawSymbol(g, x + CARD_WIDTH / 2, y + CARD_HEIGHT / 2 - CARD_HEIGHT * 2 / 9, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH / 2, y + CARD_HEIGHT / 2 + CARD_HEIGHT * 2 / 9, true, card.suit);
		
		} else {
			drawSymbol(g, x + CARD_WIDTH * 7 / 24, y + CARD_HEIGHT / 2 - CARD_HEIGHT / 3, false, card.suit);
			drawSymbol(g, x + CARD_WIDTH * 17 / 24, y + CARD_HEIGHT / 2 + CARD_HEIGHT / 3, true, card.suit);
			g.drawRect(x + CARD_WIDTH * 14 / 72, y + CARD_HEIGHT / 16, CARD_WIDTH * 44 / 72, CARD_HEIGHT * 14 / 16);
		}

	}
	
	protected void paintHand(Graphics g) {		
		int dealCards = game.dealPile.getHeight();
		
		if (dealCards == 0) {
			g.setColor(new Color(0x00FF00));
			g.fillOval(DRAW_X_POS + CARD_WIDTH / 8, DRAW_Y_POS + (CARD_HEIGHT - CARD_WIDTH) / 2 + CARD_WIDTH / 8, CARD_WIDTH * 6 / 8, CARD_WIDTH * 6 / 8);
			g.setColor(new Color(0x008000));
			g.fillOval(DRAW_X_POS + CARD_WIDTH / 6, DRAW_Y_POS + (CARD_HEIGHT - CARD_WIDTH) / 2 + CARD_WIDTH / 6, CARD_WIDTH * 4 / 6, CARD_WIDTH * 4 / 6);

		} else {
			for (int i = 0; i < dealCards / 8 + 1; ++i) {
				drawBackOfCard(g, DRAW_X_POS + 2 * i, DRAW_Y_POS + i);
			}
		}
		
		int shown = game.showingPile.getHeight() + game.discardPile.getHeight();
		
		if (shown != 0) {
			int discard = game.discardPile.getHeight();
			
			int xpos = DRAW_X_POS + TABLEAU_DISTANCE;
			int ypos = DRAW_Y_POS;
			int i = 0;
			
			Iterator<Card> reverseIter = game.discardPile.cards.descendingIterator();
			while (reverseIter.hasNext()) {
				Card card = reverseIter.next();
				drawCard(g, xpos, ypos, card, true);
				
				if (i % 8 == 7) {
					xpos += 2;
					ypos += 1;
				}
				++i;
			}
			
			reverseIter = game.showingPile.cards.descendingIterator();
			while (reverseIter.hasNext()) {
				Card card = reverseIter.next();
				drawCard(g, xpos, ypos, card, true);
				xpos += 28;
			}
		}

	}
	
	protected void paintTableau(Graphics g) {
		for (int t = 0; t < 7; ++t) {
			int ypos = TABLEAU_Y_POS;
			for (Card card : game.tableau[t].hiddenPile.cards) {
				drawBackOfCard(g, TABLEAU_X_POS + t * TABLEAU_DISTANCE, ypos);
				ypos += TABLEAU_Y_DISTANCE;
			}
			
			Iterator<Card> reverseIter = game.tableau[t].visiblePile.cards.descendingIterator();
			while (reverseIter.hasNext()) {
				Card card = reverseIter.next();
				drawCard(g, TABLEAU_X_POS + t * TABLEAU_DISTANCE, ypos, card, true);
				ypos += TABLEAU_Y_DISTANCE;
			}
		}
	}
	
	protected void paintFoundations(Graphics g) {		
		for (int i = 0; i < 4; ++i) {
			g.setColor(new Color(0x000000));
			g.drawRect(FOUNDATION_X_POS + i * TABLEAU_DISTANCE, FOUNDATION_Y_POS, CARD_WIDTH, CARD_HEIGHT);
			
			if (game.foundations[i].getHeight() != 0) {
				drawCard(g, FOUNDATION_X_POS + i * TABLEAU_DISTANCE, FOUNDATION_Y_POS, game.foundations[i].getTopCard(), true);
			}
		}
	}
	
	protected void paintHolding(Graphics g) {
		if (game.holding.getHeight() == 0) {
			return;
		}
		
		int ypos = recentMouseY + recentCardY;
		
		Iterator<Card> reverseIter = game.holding.cards.descendingIterator();
		while (reverseIter.hasNext()) {
			Card card = reverseIter.next();
			drawCard(g, recentMouseX + recentCardX, ypos, card, true);
			ypos += 15;
		}
	}
	
	protected void paintStatusBar(Graphics g) {
		String statusString = String.format("Time: %d   Score: %d", game.getTime(), game.getScore());
		
		g.setColor(new Color(0xFFFFFF));
		g.fillRect(0, WINDOW_HEIGHT - 27, WINDOW_WIDTH, 27);
		
		g.setColor(new Color(0x000000));
		g.setFont(new Font("Arial", Font.BOLD, 14));
		g.drawString(statusString, 10, WINDOW_HEIGHT - 9);
	}
	
	public void paint(Graphics g) {
		if (game == null) {
			return;
		}
		
		super.paintComponent(g);
		g.setColor(new Color(0x008000));
		g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		paintHand(g);
		paintTableau(g);
		paintFoundations(g);
		paintHolding(g);
		paintStatusBar(g);
		
		if (game.isWon()) {
			g.setColor(new Color(0xFFFF00));
			g.setFont(new Font("Arial", Font.BOLD, 48));
			g.drawString("YOU \"WINNED\"!", WINDOW_WIDTH / 2 - 150, WINDOW_HEIGHT / 2);
		}
	}
	
	public GUI.CardClickInfo detectMousePosition(int x, int y) {
		int tableau = -1;
		int cards = 0;
		int cardX = 0;
		int cardY = 0;
		
		if (y >= FOUNDATION_Y_POS && y <= FOUNDATION_Y_POS + CARD_HEIGHT) {
			if (x >= FOUNDATION_X_POS && ((x - FOUNDATION_X_POS) % TABLEAU_DISTANCE) < CARD_WIDTH) {
				cards = 1;
				tableau = Solitaire.FOUNDATION_COLUMN_BASE + ((x - FOUNDATION_X_POS) / TABLEAU_DISTANCE);
				cardX = FOUNDATION_X_POS + ((x - FOUNDATION_X_POS) / TABLEAU_DISTANCE) * TABLEAU_DISTANCE;
				cardY = FOUNDATION_Y_POS;
			}
		}
		
		int draw3Shift = game.draw3 ? DEAL_3_SHIFT_DIST * (game.showingPile.getHeight() - 1) : 0;
		if (y >= DRAW_Y_POS && y <= DRAW_Y_POS + CARD_HEIGHT + game.dealPile.getHeight() / 8) {
			if (x >= DRAW_X_POS && x <= DRAW_X_POS + CARD_WIDTH + game.dealPile.getHeight() / 4) {
				cards = 1;
				tableau = Solitaire.DRAW_PILE_BASE;
				cardX = DRAW_X_POS;
				cardY = DRAW_Y_POS;
			}
			if (x >= DRAW_X_POS + TABLEAU_DISTANCE + draw3Shift && x <= DRAW_X_POS + TABLEAU_DISTANCE + CARD_WIDTH + draw3Shift + game.discardPile.getHeight() / 4) {
				cards = 1;
				tableau = Solitaire.HAND_COLUMN_BASE;
				cardX = DRAW_X_POS + TABLEAU_DISTANCE + draw3Shift;
				cardY = DRAW_Y_POS;
			}
		}
		
		if (y >= TABLEAU_Y_POS) {
			if (x >= TABLEAU_X_POS && ((x - TABLEAU_X_POS) % TABLEAU_DISTANCE) < CARD_WIDTH) {
				tableau = ((x - TABLEAU_X_POS) / TABLEAU_DISTANCE);

				int hidden = game.tableau[tableau].hiddenPile.getHeight();
				
				int cardsIn = (y - TABLEAU_Y_POS) / TABLEAU_Y_DISTANCE;
				
				if (cardsIn - hidden >= 0) {
					cards = game.tableau[tableau].visiblePile.getHeight() + hidden - cardsIn;
					if (cards < 1) {
						cards = 1;
						cardsIn =  game.tableau[tableau].visiblePile.getHeight() + hidden;
					}
					cardX = TABLEAU_X_POS + ((x - TABLEAU_X_POS) / TABLEAU_DISTANCE) * TABLEAU_DISTANCE;
					cardY = TABLEAU_Y_POS + cardsIn * TABLEAU_Y_DISTANCE;
					
				} else {
					tableau = -1;
				}
			}
		}
		
		return new CardClickInfo(tableau, cards, cardX, cardY);
	}
	
	void start(boolean draw3) {
		game = new Solitaire(draw3);
		repaint();
	}
	
	GUI(boolean draw3) {
		game = null;
		
		BufferedImage bf = new BufferedImage(2, 2, BufferedImage.TYPE_INT_BGR);
		bf.setRGB(0, 0, 0x0000FF);
		bf.setRGB(1, 1, 0x0000FF);
		bf.setRGB(0, 1, 0x000080);
		bf.setRGB(1, 0, 0x000080);
		cardBackPaint = new TexturePaint(bf, new Rectangle(0, 0, 5, 5));
		
		setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

		JPanel gamePanel = new JPanel();
		gamePanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		add(gamePanel);
		
		(new Timer(200, new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        repaint();
		    }
		})).start();
		
		gamePanel.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (game.holding.getHeight() == 0) {
					return;
				}
				
				recentMouseX = e.getX();
				recentMouseY = e.getY();
				
				repaint();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				
			}			
		});
		
		gamePanel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {								
			}

			@Override
			public void mousePressed(MouseEvent e) {
				recentMouseX = e.getX();
				recentMouseY = e.getY();
				
				CardClickInfo clickInfo = detectMousePosition(recentMouseX, recentMouseY);
				
				if (clickInfo.tableau == Solitaire.DRAW_PILE_BASE) {
					game.flipHand(game.draw3 ? 3 : 1);
					repaint();
				
				} else if (clickInfo.tableau != -1 && game.holding.getHeight() == 0) {
					
					if (clickInfo.tableau < 7) {
						Tableau tab = game.tableau[clickInfo.tableau];
						if (tab.visiblePile.getHeight() == 0 && tab.hiddenPile.getHeight() != 0) {
							game.flipColumn(clickInfo.tableau);
							repaint();
							return;
						}
					}
					
					game.hold(clickInfo.tableau, clickInfo.cards);
					recentCardX = clickInfo.cardX - recentMouseX;
					recentCardY = clickInfo.cardY - recentMouseY;
					repaint();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				CardClickInfo clickInfo = detectMousePosition(recentMouseX, recentMouseY);
				
				if (game.holding.getHeight() != 0) {
					game.release(clickInfo.tableau);
					repaint();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {				
			}

			@Override
			public void mouseExited(MouseEvent e) {				
			}
		});
		
		game = new Solitaire(draw3);
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		JFrame frame = new JFrame("Solitaire");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		try {
			ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(new File("solitaire.exe"));	//new ImageIcon("C:/Users/Alex/source/repos/SolitaireExe/icon.png");
			frame.setIconImage(icon.getImage());
		} catch (Exception e) {
		
		}
		GUI gui = new GUI(false);

		JMenuBar menu = new JMenuBar();
		frame.setJMenuBar(menu);
		
		JMenu gameMenu = new JMenu("Game");
		menu.add(gameMenu);
		
		JMenuItem dealBtn = new JMenuItem("Deal                        ");
		dealBtn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		dealBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gui.start(gui.game.draw3);
			}
		});
		gameMenu.add(dealBtn);
		
		gameMenu.add(new JSeparator());
		
		JMenuItem deal1Btn = new JMenuItem("Play draw 1");
		deal1Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gui.start(false);
			}
		});
		gameMenu.add(deal1Btn);

		JMenuItem deal3Btn = new JMenuItem("Play draw 3");
		deal3Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gui.start(true);
			}
		});
		gameMenu.add(deal3Btn);
		
		frame.setContentPane(gui);
		frame.pack();
		frame.setVisible(true);
	}
}