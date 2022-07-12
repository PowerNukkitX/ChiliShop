package cn.innc11.chilishop.form;

import cn.nukkit.event.player.PlayerFormRespondedEvent;

public interface FormResponse
{
	void onFormResponse(PlayerFormRespondedEvent e);

	void onFormClose(PlayerFormRespondedEvent e);
}
