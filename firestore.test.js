const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const AUTHOR_UID = "author_curio";
const READER_UID = "reader_123";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Public Readers: anyone unauthenticated can read published research stories like news", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("researches").doc("post1").set({
      title: "What happens to the brain when a person is dying",
      authorEmail: "curioofficial1@gmail.com",
      likesCount: 10
    });
  });

  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertSucceeds(unauthDb.collection("researches").doc("post1").get());
  await assertSucceeds(unauthDb.collection("researches").get());
});

test("Public Readers: anyone unauthenticated can like a published research story", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("researches").doc("post1").set({
      title: "What happens to the brain when a person is dying",
      authorEmail: "curioofficial1@gmail.com",
      likesCount: 10
    });
  });

  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertSucceeds(unauthDb.collection("researches").doc("post1").update({
    likesCount: 11
  }));
});

test("Unauthenticated user: cannot publish research without signing in", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("researches").doc("post2").set({
    title: "Unauthenticated post"
  }));
});

test("Authenticated author: can publish and update research on Curio", async () => {
  const authorDb = testEnv.authenticatedContext(AUTHOR_UID).firestore();
  const ref = authorDb.collection("researches").doc("post2");
  await assertSucceeds(ref.set({
    title: "The man behind Manchester City downfall",
    authorId: AUTHOR_UID,
    authorEmail: "curioofficial1@gmail.com"
  }));
  await assertSucceeds(ref.update({
    spark: "Updated research findings"
  }));
});
