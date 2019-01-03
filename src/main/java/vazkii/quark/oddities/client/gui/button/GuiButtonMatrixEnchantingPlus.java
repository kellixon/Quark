package vazkii.quark.oddities.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import vazkii.quark.oddities.client.gui.GuiMatrixEnchanting;

public class GuiButtonMatrixEnchantingPlus extends GuiButton {

	public GuiButtonMatrixEnchantingPlus(int x, int y) {
		super(0, x, y, 50, 12, "");
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
		if(!visible)
			return;
		
		mc.renderEngine.bindTexture(GuiMatrixEnchanting.BACKGROUND);
		int u = 0;
		int v = 177;
		
		if(!enabled)
			v += 12;
		else if(hovered)
			v += 24;

		drawTexturedModalRect(x, y, u, v, width, height);
	}

}