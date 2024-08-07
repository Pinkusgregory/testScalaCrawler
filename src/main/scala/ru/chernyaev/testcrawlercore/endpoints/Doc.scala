package ru.chernyaev.testcrawlercore.endpoints

import sttp.model.StatusCode
import sttp.tapir.integ.cats.TapirCodecCats
import sttp.tapir.json.circe.TapirJsonCirce

trait Doc extends TapirJsonCirce with TapirCodecCats {

  type Err = (StatusCode, ErrorResult)

}
