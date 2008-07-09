/*
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */

package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;

/**
 * @author Yura Mamyrin
 */
public class SpriteLabel extends ProgressBar {

	private Sprite sprite;

	public SpriteLabel(Sprite sp) {
		
		sprite = sp;
		
		width = sprite.getWidth();
		height = sprite.getHeight();
		
		loaded = sprite.getFrameSequenceLength()-1;
		
	}
	
	public void paintComponent(Graphics g) {

		sprite.setFrame(loading);
		sprite.paint(g);

	}

}
