package it.metaglio.harlemdroidsample;

import it.metaglio.harlemdroid.Harlem;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	public void shake(View v) {

		Harlem.shake(this);
	}

	public void shock(View v) {

		Harlem.shock(this);
	}
}
