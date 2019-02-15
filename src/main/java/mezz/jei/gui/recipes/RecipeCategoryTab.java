package mezz.jei.gui.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

import mezz.jei.Internal;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IModIdHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.ingredients.IngredientRegistry;

public class RecipeCategoryTab extends RecipeGuiTab {
	private final IRecipeGuiLogic logic;
	private final IRecipeCategory category;

	public RecipeCategoryTab(IRecipeGuiLogic logic, IRecipeCategory category, int x, int y) {
		super(x, y);
		this.logic = logic;
		this.category = category;
	}

	@Override
	public boolean handleMouseClicked(double mouseX, double mouseY, int mouseButton) {
		logic.setRecipeCategory(category);
		SoundHandler soundHandler = Minecraft.getInstance().getSoundHandler();
		soundHandler.play(SimpleSound.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		return true;
	}

	@Override
	public void draw(boolean selected, int mouseX, int mouseY) {
		super.draw(selected, mouseX, mouseY);

		int iconX = x + 4;
		int iconY = y + 4;

		IDrawable icon = category.getIcon();
		//noinspection ConstantConditions
		if (icon != null) {
			iconX += (16 - icon.getWidth()) / 2;
			iconY += (16 - icon.getHeight()) / 2;
			icon.draw(iconX, iconY);
		} else {
			List<Object> recipeCatalysts = logic.getRecipeCatalysts(category);
			if (!recipeCatalysts.isEmpty()) {
				Object ingredient = recipeCatalysts.get(0);
				renderIngredient(iconX, iconY, ingredient);
			} else {
				String text = category.getTitle().substring(0, 2);
				Minecraft minecraft = Minecraft.getInstance();
				FontRenderer fontRenderer = minecraft.fontRenderer;
				float textCenterX = x + (TAB_WIDTH / 2f);
				float textCenterY = y + (TAB_HEIGHT / 2f) - 3;
				int color = isMouseOver(mouseX, mouseY) ? 0xFFFFA0 : 0xE0E0E0;
				fontRenderer.drawStringWithShadow(text, textCenterX - fontRenderer.getStringWidth(text) / 2f, textCenterY, color);
				GlStateManager.color4f(1, 1, 1, 1);
			}
		}
	}

	private static <T> void renderIngredient(int iconX, int iconY, T ingredient) {
		IngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
		IIngredientRenderer<T> ingredientRenderer = ingredientRegistry.getIngredientRenderer(ingredient);
		GlStateManager.enableDepthTest();
		ingredientRenderer.render(iconX, iconY, ingredient);
		GlStateManager.enableAlphaTest();
		GlStateManager.disableDepthTest();
	}

	@Override
	public boolean isSelected(IRecipeCategory selectedCategory) {
		return category.getUid().equals(selectedCategory.getUid());
	}

	@Override
	public List<String> getTooltip() {
		List<String> tooltip = new ArrayList<>();
		String title = category.getTitle();
		//noinspection ConstantConditions
		if (title != null) {
			tooltip.add(title);
		}

		ResourceLocation uid = category.getUid();
		String modId = uid.getNamespace();
		IModIdHelper modIdHelper = Internal.getHelpers().getModIdHelper();
		if (modIdHelper.isDisplayingModNameEnabled()) {
			String modName = modIdHelper.getFormattedModNameForModId(modId);
			tooltip.add(modName);
		}
		return tooltip;
	}
}
